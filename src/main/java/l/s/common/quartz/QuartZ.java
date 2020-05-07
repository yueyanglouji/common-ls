package l.s.common.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.Key;

public class QuartZ {
	
	private Scheduler scheduler;
	
	private boolean start;
	
	private static QuartZ quartZ;
	
	private QuartZ(){

    	try {
            // Grab the Scheduler instance from the Factory
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    
	}
	
	public static QuartZ getInstance(){
		if(quartZ == null){
			quartZ = new QuartZ();
		}
		return quartZ;
	}
	
	public QuartZ start() throws Exception{
		if(!start){
			start = true;
			scheduler.start();
		}
		return this;
	}
	
	public void shutdown() throws Exception{
		scheduler.shutdown();
	}
	
	public void shutdownnow() throws Exception{
		scheduler.shutdown(false);
	}
	
	public static JobDetail createJob(Runnable runnable){
		// Build JobDetail instance.
		JobDetailImpl jdi = new JobDetailImpl();
		JobKey key = new JobKey(Key.createUniqueName(null), null);
		jdi.setKey(key); 
		
		jdi.setJobClass(QuarZMethodInvokingJob.class);
		jdi.setDurability(true);
		jdi.getJobDataMap().put("methodInvokerObject", runnable);
		
		return jdi;
	}
	
	public QuartZ submit(Runnable runnable) throws Exception{
		
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(new TriggerKey(Key.createUniqueName(null), null))
            .startNow()
            .withSchedule(simpleScheduleBuilder)       
            .build();
        
		scheduler.scheduleJob(createJob(runnable), trigger);
		
		return this;
	}
	
	public QuartZ submit(Runnable runnable, long dalay) throws Exception{
		
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(dalay).repeatForever();
        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(new TriggerKey(Key.createUniqueName(null), null))
            .startNow()
            .withSchedule(simpleScheduleBuilder)       
            .build();
        
		scheduler.scheduleJob(createJob(runnable), trigger);
		
		return this;
	}
	
	public QuartZ submit(Runnable runnable, long dalay, int repeatCount) throws Exception{
		
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(dalay).withRepeatCount(repeatCount);
        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(new TriggerKey(Key.createUniqueName(null), null))
            .startNow()
            .withSchedule(simpleScheduleBuilder)       
            .build();
        
		scheduler.scheduleJob(createJob(runnable), trigger);
		
		return this;
	}
	
	public QuartZ submit(Runnable runnable, String cron) throws Exception{
		
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(new TriggerKey(Key.createUniqueName(null), null))
            .startNow()
            .withSchedule(cronScheduleBuilder)            
            .build();
        
		scheduler.scheduleJob(createJob(runnable), trigger);
		
		return this;
	}
	
	public QuartZ submit(Runnable runnable, Trigger trigger) throws Exception{
		scheduler.scheduleJob(createJob(runnable), trigger);
		
		return this;
	}
	
	public void waitOverShutdown() throws Exception{
		scheduler.shutdown(true);
	}
	
	public void waitOverOneTimes(QuartZBlock block, QuartZBlock...blocks) throws Exception{
		List<QuartZBlock> list = new ArrayList<>();
		list.add(block);
		list.addAll(Arrays.asList(blocks));
		while(true){
			boolean flg = true;
			for(int i=0;i<list.size();i++){
				QuartZBlock b = list.get(i);
				if(b.getComplateTimes() < 1){
					flg = false;
					break;
				}
			}
			if(flg){
				break;
			}
			
			Thread.sleep(2000);
		}
	}
	
}
