package l.s.common.httpclient;

public class Response {

	private boolean success = true;
	
	private ResponseHeader header;
	
	private ResponseContent content;
	
	private Exception exception;
	
	public ResponseHeader getHeader() {
		return header;
	}

	public void setHeader(ResponseHeader header) {
		this.header = header;
	}

	public ResponseContent getContent() {
		return content;
	}

	public void setContent(ResponseContent content) {
		this.content = content;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
