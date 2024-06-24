package l.s.common.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
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

	private final List<TriggerKey> triggerKeyList;

	private final Properties properties;
	
	private QuartZ(){
		properties = new Properties();
		properties.setProperty("org.quartz.threadPool.threadCount", "5");
		triggerKeyList = new ArrayList<>();
	}
	
	public static QuartZ getInstance(){
		if(quartZ == null){
			quartZ = new QuartZ();
		}
		return quartZ;
	}

	/**
	 * @param threadCount the number of thread. Default 5
	 * @return this
	 */
	public QuartZ withThreadCount(int threadCount){
		properties.setProperty("org.quartz.threadPool.threadCount", threadCount + "");
		return this;
	}

	public QuartZ withConfigProperties(String key, String value){
		properties.setProperty(key, value);
		return this;
	}
	
	public QuartZ start() throws Exception{
		if(!start){
			start = true;
			// Grab the Scheduler instance from the Factory
			StdSchedulerFactory factory = new StdSchedulerFactory();
			factory.initialize(properties);
			scheduler = factory.getScheduler();
			scheduler.start();
		}
		return this;
	}
	
	public void shutdown() throws Exception{
		scheduler.shutdown();
	}
	
	public void shutdownNow() throws Exception{
		scheduler.shutdown(false);
	}
	
	public JobDetail createJob(Runnable runnable){
		// Build JobDetail instance.
		JobDetailImpl jdi = new JobDetailImpl();
		JobKey key = new JobKey(Key.createUniqueName(null), null);
		jdi.setKey(key); 
		
		jdi.setJobClass(QuarZMethodInvokingJob.class);
		jdi.setDurability(true);
		jdi.getJobDataMap().put("methodInvokerObject", runnable);
		jdi.getJobDataMap().put("quartZ", this);
		return jdi;
	}
	
	public JobInfo submit(Runnable runnable) throws Exception{
		
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(new TriggerKey(Key.createUniqueName(null), null))
            .startNow()
            .withSchedule(simpleScheduleBuilder)       
            .build();

		return submitJob(createJob(runnable), trigger);
	}

	public JobInfo trigger(JobDetail jobDetail) throws Exception{

		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
		// Trigger the job to run now, and then repeat every 40 seconds
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(new TriggerKey(Key.createUniqueName(null), null))
				.startNow()
				.withSchedule(simpleScheduleBuilder)
				.forJob(jobDetail)
				.build();
		triggerJob(trigger);

		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobDetail(jobDetail);
		jobInfo.setTrigger(trigger);
		return jobInfo;
	}

	public JobInfo submit(Runnable runnable, long delay) throws Exception{
		return submit(runnable, delay, 0, null, null);
	}

	public JobInfo trigger(JobDetail jobDetail, long delay) throws Exception{
		return trigger(jobDetail, delay, 0, null, null);
	}
	
	public JobInfo submit(Runnable runnable, long delay, Date startTime, Date endTime) throws Exception{
		return submit(runnable, delay, 0, startTime, endTime);
	}

	public JobInfo trigger(JobDetail jobDetail, long delay, Date startTime, Date endTime) throws Exception{
		return trigger(jobDetail, delay, 0, startTime, endTime);
	}

	public JobInfo submit(Runnable runnable, long delay, boolean repeatForever) throws Exception{
		return submit(runnable, delay, repeatForever, null, null);
	}

	public JobInfo trigger(JobDetail jobDetail, long delay, boolean repeatForever) throws Exception{
		return trigger(jobDetail, delay, repeatForever, null, null);
	}

	public JobInfo submit(Runnable runnable, long delay, boolean repeatForever, Date startTime, Date endTime) throws Exception{
		if(repeatForever){
			return submit(runnable, delay, -1, startTime, endTime);
		}else {
			return submit(runnable, delay, 0, startTime, endTime);
		}
	}

	public JobInfo trigger(JobDetail jobDetail, long delay, boolean repeatForever, Date startTime, Date endTime) throws Exception{
		if(repeatForever){
			return trigger(jobDetail, delay, -1, startTime, endTime);
		}else {
			return trigger(jobDetail, delay, 0, startTime, endTime);
		}
	}

	public JobInfo submit(Runnable runnable, long delay, int repeatCount) throws Exception{
		return submit(runnable, delay, repeatCount, null, null);
	}

	public JobInfo trigger(JobDetail jobDetail, long delay, int repeatCount) throws Exception{
		return trigger(jobDetail, delay, repeatCount, null, null);
	}
	
	public JobInfo submit(Runnable runnable, long delay, int repeatCount, Date startTime, Date endTime) throws Exception{
		SynSimpleScheduleBuilder simpleScheduleBuilder = SynSimpleScheduleBuilder.simpleSchedule();
		if(delay > 0){
			simpleScheduleBuilder.withIntervalInMilliseconds(delay);
		}
		if(repeatCount == -1){
			if(delay <= 0){
				simpleScheduleBuilder.withIntervalInMilliseconds(1000);
			}
			simpleScheduleBuilder.repeatForever();
		}else if(repeatCount > 0){
			if(delay <= 0){
				simpleScheduleBuilder.withIntervalInMilliseconds(1000);
			}
			simpleScheduleBuilder.withRepeatCount(repeatCount);
		}

		Trigger trigger = createTrigger(startTime, endTime, simpleScheduleBuilder);

		return submitJob(createJob(runnable), trigger);
	}

	public JobInfo trigger(JobDetail jobDetail, long delay, int repeatCount, Date startTime, Date endTime) throws Exception{
		SynSimpleScheduleBuilder simpleScheduleBuilder = SynSimpleScheduleBuilder.simpleSchedule();
		if(delay > 0){
			simpleScheduleBuilder.withIntervalInMilliseconds(delay);
		}
		if(repeatCount == -1){
			if(delay <= 0){
				simpleScheduleBuilder.withIntervalInMilliseconds(1000);
			}
			simpleScheduleBuilder.repeatForever();
		}else if(repeatCount > 0){
			if(delay <= 0){
				simpleScheduleBuilder.withIntervalInMilliseconds(1000);
			}
			simpleScheduleBuilder.withRepeatCount(repeatCount);
		}

		Trigger trigger = createTrigger(startTime, endTime, jobDetail.getKey(), simpleScheduleBuilder);
		triggerJob(trigger);

		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobDetail(jobDetail);
		jobInfo.setTrigger(trigger);
		return jobInfo;
	}

	public JobInfo submit(Runnable runnable, String cron) throws Exception{
		return submit(runnable, cron, null, null, null);
	}

	public JobInfo trigger(JobDetail jobDetail, String cron) throws Exception{
		return trigger(jobDetail, cron, null, null, null);
	}

	public JobInfo submit(Runnable runnable, String cron, Date startTime, Date endTime) throws Exception{
		return submit(runnable, cron, startTime, endTime, null);
	}

	public JobInfo trigger(JobDetail jobDetail, String cron, Date startTime, Date endTime) throws Exception{
		return trigger(jobDetail, cron, startTime, endTime, null);
	}

	public JobInfo submit(Runnable runnable, String cron, TimeZone timeZone) throws Exception{
		return submit(runnable, cron, null, null, timeZone);
	}

	public JobInfo trigger(JobDetail jobDetail, String cron, TimeZone timeZone) throws Exception{
		return trigger(jobDetail, cron, null, null, timeZone);
	}

	public JobInfo submit(Runnable runnable, String cron, Date startTime, Date endTime, TimeZone timeZone) throws Exception{
		
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
				.withMisfireHandlingInstructionDoNothing();
		if(timeZone != null){
			cronScheduleBuilder.inTimeZone(timeZone);
		}
		Trigger trigger = createTrigger(startTime, endTime, cronScheduleBuilder);

		return submitJob(createJob(runnable), trigger);
	}

	public JobInfo trigger(JobDetail jobDetail, String cron, Date startTime, Date endTime, TimeZone timeZone) throws Exception{

		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
				.withMisfireHandlingInstructionDoNothing();
		if(timeZone != null){
			cronScheduleBuilder.inTimeZone(timeZone);
		}
		Trigger trigger = createTrigger(startTime, endTime, jobDetail.getKey(), cronScheduleBuilder);
		triggerJob(trigger);

		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobDetail(jobDetail);
		jobInfo.setTrigger(trigger);
		return jobInfo;
	}

	private Trigger createTrigger(Date startTime, Date endTime, ScheduleBuilder<?> schedBuilder){
		return this.createTrigger(startTime, endTime, null, schedBuilder);
	}

	private Trigger createTrigger(Date startTime, Date endTime, JobKey jobKey, ScheduleBuilder<?> schedBuilder){
		// Trigger the job to run now, and then repeat every 40 seconds
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withIdentity(new TriggerKey(Key.createUniqueName(null), null));
		if(startTime != null){
			triggerBuilder.startAt(startTime);
		}else {
			triggerBuilder.startNow();
		}
		if(endTime != null){
			triggerBuilder.endAt(endTime);
		}
		triggerBuilder.withSchedule(schedBuilder);
		if(jobKey != null){
			triggerBuilder.forJob(jobKey);
		}
		return triggerBuilder.build();
	}

	public JobInfo submit(Runnable runnable, Trigger trigger) throws Exception{
		return submitJob(createJob(runnable), trigger);
	}

	public JobInfo submitJob(JobDetail job, Trigger trigger) throws Exception{
		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobDetail(job);
		jobInfo.setTrigger(trigger);
		return submitJob(jobInfo);
	}

	public void triggerJob(Trigger trigger) throws Exception{
		scheduler.scheduleJob(trigger);
		this.triggerKeyList.add(trigger.getKey());
	}

	public Date getNextFireTime(JobDetail job) throws SchedulerException {
		List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
		Date nextFireTime = null;
		for(Trigger trigger : triggersOfJob){
			if(nextFireTime == null){
				nextFireTime = trigger.getNextFireTime();
			}else {
				Date next = trigger.getNextFireTime();
				if(next.compareTo(nextFireTime) < 0){
					nextFireTime = next;
				}
			}
		}
		return nextFireTime;
	}

	public JobInfo submitJob(JobInfo jobInfo) throws Exception{
		scheduler.scheduleJob(jobInfo.getJobDetail(), jobInfo.getTrigger());
		this.triggerKeyList.add(jobInfo.getTrigger().getKey());
		return jobInfo;
	}

	public JobInfo triggerJobOnceNow(JobDetail jobInfo) throws Exception{
		return trigger(jobInfo);
	}

	public QuartZ pauseJob(JobInfo jobInfo) throws Exception{
		scheduler.pauseJob(jobInfo.getJobDetail().getKey());
		return this;
	}

	public QuartZ resumeJob(JobInfo jobInfo) throws Exception{
		scheduler.resumeJob(jobInfo.getJobDetail().getKey());
		return this;
	}

	public QuartZ pauseTrigger(JobInfo jobInfo) throws Exception{
		scheduler.pauseTrigger(jobInfo.getTrigger().getKey());
		return this;
	}

	public QuartZ resumeTrigger(JobInfo jobInfo) throws Exception{
		scheduler.resumeTrigger(jobInfo.getTrigger().getKey());
		return this;
	}

	public QuartZ pauseAll() throws Exception{
		scheduler.pauseAll();
		return this;
	}

	public QuartZ resumeAll() throws Exception{
		scheduler.resumeAll();
		return this;
	}

	public QuartZ deleteJob(JobDetail job) throws Exception{
		List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
		scheduler.deleteJob(job.getKey());
		for(Trigger trigger: triggersOfJob){
			this.triggerKeyList.remove(trigger.getKey());
		}
		return this;
	}

	public QuartZ clear() throws Exception{
		scheduler.clear();
		this.triggerKeyList.clear();
		return this;
	}

	public QuartZ interrupt(JobInfo job) throws Exception{
		scheduler.interrupt(job.getJobDetail().getKey());
		return this;
	}

	public QuartZ deleteTrigger(Trigger trigger) throws Exception{
		return deleteTrigger(trigger.getKey());
	}

	public QuartZ deleteTrigger(TriggerKey triggerKey) throws Exception{
		boolean b = scheduler.unscheduleJob(triggerKey);
		if(b){
			this.triggerKeyList.remove(triggerKey);
		}
		return this;
	}
	
	public void waitOverShutdown() throws Exception{
		scheduler.shutdown(true);
	}

	public void waitOverShutdownIfNoMoreJob() throws Exception{
		while (true){
			boolean shutdown = true;
			try{
				for(TriggerKey key : triggerKeyList){
					try {
						Trigger.TriggerState stat = scheduler.getTriggerState(key);
						if(stat == Trigger.TriggerState.NORMAL){
							shutdown = false;
						}
					}catch (SchedulerException e){
						// ignore
					}
				}
			}catch (Throwable e){
				continue;
			}

			if(shutdown){
				shutdown();
				break;
			}else {
				TimeUnit.SECONDS.sleep(2);
			}
		}

	}
	
	public void waitOverOneTimes(QuartZBlock block, QuartZBlock...blocks) throws Exception{
		List<QuartZBlock> list = new ArrayList<>();
		list.add(block);
		list.addAll(Arrays.asList(blocks));
		while(true){
			boolean flg = true;
			for(int i=0;i<list.size();i++){
				QuartZBlock b = list.get(i);
				if(b.getCompleteTimes() < 1){
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
