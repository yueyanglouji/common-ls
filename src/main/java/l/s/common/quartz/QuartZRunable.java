package l.s.common.quartz;

public  interface QuartZRunable extends Runnable{

	default Object getResult(){
		return null;
	}
	
}
