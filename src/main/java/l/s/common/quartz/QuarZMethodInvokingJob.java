package l.s.common.quartz;

import l.s.common.context.ThreadLocalContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;

public class QuarZMethodInvokingJob extends QuartzJobBean{
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private Runnable methodInvokerObject;

	private QuartZ quartZ;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		if(QuartZBlockRunnable.class.isAssignableFrom(methodInvokerObject.getClass())){
			QuartZBlockRunnable r = (QuartZBlockRunnable)methodInvokerObject;
			if(r.isRunning()){
				log.debug("running...   =>skip this thread");
				return;
			}
		}
		ThreadLocalContext.getContext().reset();
		JobExecutionContextUtil.setContext(context);
		try{
			methodInvokerObject.run();
		}catch (Throwable e){

		}
		JobExecutionContextUtil.removeContext();
		
		if(QuartZBlock.class.isAssignableFrom(methodInvokerObject.getClass())){
			QuartZRunnable r = (QuartZRunnable)methodInvokerObject;
			context.setResult(r.getResult());
		}

		JobKey key = context.getJobDetail().getKey();
		try {
			List<? extends Trigger> triggersOfJob = context.getScheduler().getTriggersOfJob(key);
			for(Trigger trigger : triggersOfJob){
				Date nextFireTime = trigger.getNextFireTime();
				if(nextFireTime == null){
					quartZ.deleteTrigger(trigger.getKey());
					log.info("delete trigger: " + trigger.getKey());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public Runnable getMethodInvokerObject() {
		return methodInvokerObject;
	}

	public void setMethodInvokerObject(Runnable methodInvokerObject) {
		this.methodInvokerObject = methodInvokerObject;
	}

	public QuartZ getQuartZ() {
		return quartZ;
	}

	public void setQuartZ(QuartZ quartZ) {
		this.quartZ = quartZ;
	}

}
