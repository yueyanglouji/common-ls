package l.s.common.httpclient.common;

import l.s.common.httpclient.HttpClientType;
import org.apache.hc.core5.http.HttpVersion;

public class DownloadDevice {

	private volatile float rate;

	private volatile float size;
	
	private volatile Response response;
	
	private volatile Throwable exception;
	
	private volatile boolean status;
	
	private volatile boolean waite = true;

	private final HttpClientType httpClientType;

	public DownloadDevice(HttpClientType httpClientType){
		this.httpClientType = httpClientType;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public String getSize(){
		return String.format("%.2f", size) + "MB";
	}
	public float getRate(){
		if(httpClientType == HttpClientType.HTTP_2_CLIENT){
			return -1;
		}
		if(rate == 1){
			return 1;
		}
		float tmp = rate;
		if(tmp < 0){
			return 0;
		}else if(tmp > 1){
			return 0;
		}
		return tmp;
	}

	public Response getOriginalResponse() {
		return response;
	}

	public Throwable getException() {
		return exception;
	}

	public boolean status() {
		return status;
	}

	public boolean isWaite() {
		return waite;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setWaite(boolean waite) {
		this.waite = waite;
	}

}
