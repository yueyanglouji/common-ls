package l.s.common.httpclient.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeader {

	private String httpVersion;
	
	private String statusCode;
	
	private String statusDescription;
	
	private Map<String, List<String>> headers;
	
	public ResponseHeader(){
		
		this.headers = new LinkedHashMap<>();
	}	
	
	public void addHeader(String key, String value){
		List<String> list = getHeaderByCaseInsensitive(key);
		if(list == null){
			list = new ArrayList<>();
			this.headers.put(key, list);
		}
		
		list.add(value);
	}
	
	public void setHeader(Map<String, List<String>> headers){
		this.headers = headers;
	}
	
	public String getHeadersToString(){
		StringBuilder builder = new StringBuilder();
		builder.append(this.httpVersion).append(" ").append(this.statusCode);
		if(this.statusDescription != null){
			builder.append(" ").append(this.statusDescription);
		}
		builder.append("\n");
		for(Entry<String, List<String>> e: headers.entrySet()){
			String key = e.getKey();
			if(key != null){
				builder.append(key);
				builder.append(":");
			}
			for(String value : e.getValue()){
				builder.append(value);
				builder.append(";");
			}
			if(builder.charAt(builder.length()-1) == ';'){
				builder.deleteCharAt(builder.length()-1);
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
	public Map<String, List<String>> getHeaderCollection(){
		return headers;
	}
	
	private List<String> getHeaderByCaseInsensitive(String key){
		
		if(key == null){
			return headers.get(key);
		}
		
		for(Entry<String, List<String>> e : headers.entrySet()){
			if(e.getKey() == null){
				if(key == null){
					return e.getValue();
				}
				continue;
			}
			
			if(e.getKey().toLowerCase().equals(key.toLowerCase())){
				return e.getValue();
			}
		}

		return null;
	}
	
	public String getHeader(String key){

		List<String> list = getHeaderByCaseInsensitive(key);
				
		if(list == null || list.size()==0){
			return null;
		}else{
			StringBuilder builder = new StringBuilder();
			for(String value : list){
				builder.append(value);
				builder.append(";");
			}
			if(builder.charAt(builder.length()-1) == ';'){
				builder.deleteCharAt(builder.length()-1);
			}
			
			return builder.toString();
		}
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
}
