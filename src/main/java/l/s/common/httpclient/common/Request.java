package l.s.common.httpclient.common;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Request {

	private final RequestHeader defaultHeader;

	private final RequestHeader header;
	
	private final RequestParam param;

	private final RequestParam urlParam;

	private final HttpUpInputPutStream stream;
	
	private URL url;
	
	private RequestMethod method;
	
	public Request(URL url){
		this(url, new RequestHeader());
	}

	public Request(URL url, RequestHeader defaultHeader){

		this.url = url;

		this.defaultHeader = defaultHeader;

		this.header = new RequestHeader();

		this.param = new RequestParam();

		this.urlParam = new RequestParam();

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

	public void addParam(String key, String value){
		this.param.addParam(key, value);
	}

	public void addUrlParam(String key, String value){
		this.urlParam.addParam(key, value);
	}
	
	public void stream(String txt) throws Exception{
		stream.add(txt);
	}
	
	public void stream(InputStream in) throws Exception{
		stream.add(in);
	}

	public Map<String, String> getHeadersWithLowercaseKey(){
		return getHeaders(true);
	}

	public Map<String, String> getHeaders(){
		return getHeaders(false);
	}

	private Map<String, String> getHeaders(boolean lowercaseKey){
		Map<String, List<String>> map = header.getHeaders();

		Map<String, String> ret = new LinkedHashMap<>();
		for(Entry<String, List<String>> e: map.entrySet()){
			String key = e.getKey();
			composeCookie(lowercaseKey, ret, e, key);
		}

		map = defaultHeader.getHeaders();
		for(Entry<String, List<String>> e: map.entrySet()){
			String key = e.getKey();
			if(ret.get(key) != null){
				continue;
			}
			composeCookie(lowercaseKey, ret, e, key);
		}

		return ret;
		
	}

	private void composeCookie(boolean lowercaseKey, Map<String, String> ret, Entry<String, List<String>> e, String key) {
		StringBuilder builder = new StringBuilder();
		for(String value: e.getValue()){
			builder.append(value);
			builder.append(";");
		}
		if(builder.charAt(builder.length()-1) == ';'){
			builder.deleteCharAt(builder.length()-1);
		}
		if(lowercaseKey){
			ret.put(key.toLowerCase(), builder.toString());
		}else{
			ret.put(key, builder.toString());
		}
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

	public RequestParam getUrlParam() {
		return urlParam;
	}
}
