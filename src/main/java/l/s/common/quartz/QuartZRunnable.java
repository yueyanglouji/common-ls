package l.s.common.quartz;

public  interface QuartZRunnable extends Runnable{

	default Object getResult(){
		return null;
	}
	
}
