package l.s.common.util;

public class Retry<T>{
	
	public T toTry(RetryFunc<T> func, int times, int sleep){
		
		Exception ex = null;
		
		if(times <= 1){
			times = 1;
		}
		for(int i=0;i<times;i++){
			try{
				T t = func.function();
				return t;
			}catch(Exception e){
				ex = e;
				
				if(i == times - 1){
					break;
				}
				
				try {
					Thread.sleep(sleep);
				} catch (Exception e1) {
				}
				
				continue;
			}
		}
		
		throw new RetryOutOfTimesException(ex);
	}
}
