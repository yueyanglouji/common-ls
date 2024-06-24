package l.s.common.util;

import java.util.HashMap;
import java.util.Map;

public class LambdaSupport {

	private long accumulator;
	
	private final Map<String, Object> map;
	
	public LambdaSupport(){
		this.map = new HashMap<>();
		accumulator = 0;
	}
	
	public void setAttribute(String key, Object value){
		this.map.put(key, value);
	}
	
	public Object getAttribute(String key){
		return this.map.get(key);
	}
	
	public long getIndex(){
		return accumulator;
	}
	
	public long indexNext(){
		accumulator ++ ;
		return accumulator;
	}
	
}
