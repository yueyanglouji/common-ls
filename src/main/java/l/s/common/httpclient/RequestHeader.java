package l.s.common.httpclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

	private Map<String, List<String>> headers;
	
	public RequestHeader(){
		
		this.headers = new HashMap<String, List<String>>();
	}	
	
	public void setHeader(String key, String value) {
		this.headers.remove(key);
		List<String> list = new ArrayList<String>();
		this.headers.put(key, list);
		list.add(value);
	}
	
	public void addHeader(String key, String value){
		List<String> list = this.headers.get(key);
		if(list == null){
			list = new ArrayList<String>();
			this.headers.put(key, list);
		}
		
		list.add(value);
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public Map<String, List<String>> getHeadersByLowercaseKey() {
		Map<String, List<String>> map = new HashMap<>();
		for(Map.Entry<String, List<String>> en : headers.entrySet()){
			String key = en.getKey();
			map.put(key.toLowerCase(), en.getValue());
		}
		return map;
	}

}
