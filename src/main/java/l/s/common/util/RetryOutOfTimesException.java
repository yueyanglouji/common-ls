package l.s.common.util;

public class RetryOutOfTimesException extends RuntimeException{

	private static final long serialVersionUID = -7385145778616808231L;

	public RetryOutOfTimesException(Exception e){
		super("times out faild.", e);
	}
	
	public RetryOutOfTimesException(String message, Exception e){
		super(message, e);
	}
}
