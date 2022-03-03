package l.s.common.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuarZMethodInvokingJob extends QuartzJobBean{
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private Runnable methodInvokerObject;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		if(QuartZBlockRunnable.class.isAssignableFrom(methodInvokerObject.getClass())){
			QuartZBlockRunnable r = (QuartZBlockRunnable)methodInvokerObject;
			if(r.isRunning()){
				log.debug("running...   =>skip this thread");
				return;
			}
		}
		methodInvokerObject.run();
		
		if(QuartZBlock.class.isAssignableFrom(methodInvokerObject.getClass())){
			QuartZRunnable r = (QuartZRunnable)methodInvokerObject;
			context.setResult(r.getResult());
		}
	}

	public Runnable getMethodInvokerObject() {
		return methodInvokerObject;
	}

	public void setMethodInvokerObject(Runnable methodInvokerObject) {
		this.methodInvokerObject = methodInvokerObject;
	}
	
}
