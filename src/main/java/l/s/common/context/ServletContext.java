package l.s.common.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServletContext implements Serializable{
	
	private static final long serialVersionUID = -258225202843561999L;

	private static final ThreadLocal<ServletContext> servletContext = new ThreadLocal<>();
	
	private Map<String, Object> headers;
	
	private Map<String, Object> parameters;
	
	private String requestUrl;
	
	private String clientHost;
	
	private Object requestEntity;
	
	private Object responseEntity;
	
	private ServletContext(){
		this.headers = new HashMap<>();
		this.parameters = new HashMap<>();
	}
	
	public static ServletContext getContext() {
		if(servletContext.get() == null){
			servletContext.set(new ServletContext());
		}
        return servletContext.get();
    }

	public Map<String, Object> getHeaders() {
		return headers;
	}
	
	public Object getHeader(String key){
		return headers.get(key);
	}

	public void addHeaders(String key, Object value) {
		this.headers.put(key, value);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Object getParameter(String key){
		return parameters.get(key);
	}
	
	public void addParameters(String key, Object value) {
		this.parameters.put(key, value);
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getClientHost() {
		return clientHost;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public Object getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(Object requestEntity) {
		this.requestEntity = requestEntity;
	}

	public Object getResponseEntity() {
		return responseEntity;
	}

	public void setResponseEntity(Object responseEntity) {
		this.responseEntity = responseEntity;
	}

}
