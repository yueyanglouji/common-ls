package l.s.common.httpclient;

public enum RequestMethod {

	POST("POST"),
	
	GET("GET"),
	
	PUT("PUT"),
	
	DELETE("DELETE");
	
	private String method;
	
	private RequestMethod(String method){
		this.method = method;
	}
	
	public String getMethd(){
		return method;
	}
}
