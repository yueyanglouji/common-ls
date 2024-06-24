package l.s.common.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QuartZBlock implements QuartZBlockRunnable{

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	boolean b;
	
	long completeTimes;
	
	@Override
	public final boolean isRunning() {
		return b;
	}
	
	public long getCompleteTimes(){
		return completeTimes;
	}
	
	Object result;
	
	@Override
	public Object getResult() {
		return result;
	}
	
	public void setResult(Object result){
		this.result = result;
	}

	@Override
	public final void run() {
		if(b){
			log.debug("running...   =>skip this thread");
			return;
		}
		
		b = true;
		try {
			runz();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		completeTimes ++;
		b = false;
	}
	
	public abstract void runz() throws Exception;

	
}
