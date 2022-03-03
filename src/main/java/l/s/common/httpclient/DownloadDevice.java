package l.s.common.httpclient;

public class DownloadDevice{

	private float rate;
	
	private Response response;
	
	private Throwable exception;
	
	private boolean status;
	
	private boolean waite = true;

	public DownloadDevice(){
		
	}
	
	public float getRate(){
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

	void setRate(float rate) {
		this.rate = rate;
	}

	void setResponse(Response response) {
		this.response = response;
	}

	void setException(Throwable exception) {
		this.exception = exception;
	}

	void setStatus(boolean status) {
		this.status = status;
	}

	void setWaite(boolean waite) {
		this.waite = waite;
	}

}
