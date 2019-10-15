package l.s.common.httpclient;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Request {

	private RequestHeader header;
	
	private RequestParam param;
	
	private HttpUpInputPutStream stream;
	
	private URL url;
	
	private RequestMethod method;
	
	public Request(URL url){
		
		this.url = url;
		
		this.header = new RequestHeader();
		
		this.param = new RequestParam();
		
		this.stream = new HttpUpInputPutStream();
	}
	
	public void setHeader(String key,String value){
		this.header.setHeader(key, value);
	}
	
	public void addHeader(String key,String value){
		this.header.addHeader(key, value);
	}
	
	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public void addParam(String key,String value){
		this.param.addParam(key, value);
	}
	
	public void stream(String txt) throws Exception{
		stream.add(txt);
	}
	
	public void stream(InputStream in) throws Exception{
		stream.add(in);
	}
	
	public Map<String, String> getHeaders(){
		Map<String, List<String>> map = header.getHeaders();
		
		Map<String, String> ret = new HashMap<String, String>();
		for(Entry<String, List<String>> e: map.entrySet()){
			String key = e.getKey();
			StringBuilder builder = new StringBuilder();
			for(String value: e.getValue()){
				builder.append(value);
				builder.append(";");
			}
			if(builder.charAt(builder.length()-1) == ';'){
				builder.deleteCharAt(builder.length()-1);
			}
			
			ret.put(key, builder.toString());
		}
		
		return ret;
		
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public HttpUpInputPutStream getUpStream() throws UnsupportedEncodingException{
		
		return stream;
	}

	public RequestParam getParam() {
		return this.param;
	}
	
}
