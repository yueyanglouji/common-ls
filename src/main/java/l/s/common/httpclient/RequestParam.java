package l.s.common.httpclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParam {

	private Map<String, List<String>> param;
	
	public RequestParam(){
		this.param = new HashMap<String, List<String>>();
	}
	
	public void addParam(String key, String value){
		
		List<String> list = param.get(key);
		if(list == null){
			list = new ArrayList<String>();
			param.put(key, list);
		}
		list.add(value);
	}

	public Map<String, List<String>> getParam() {
		return param;
	}
	
}
