package l.s.common.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QuartZBlock implements QuartZBlockRunable{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	boolean b;
	
	long complateTimes;
	
	@Override
	public final boolean isRuning() {
		return b;
	}
	
	public long getComplateTimes(){
		return complateTimes;
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
			log.debug("runing...   =>skip this thread");
			return;
		}
		
		b = true;
		try {
			runz();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		complateTimes ++;
		b = false;
	}
	
	public abstract void runz() throws Exception;

	
}
