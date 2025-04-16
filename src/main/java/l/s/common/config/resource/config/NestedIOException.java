package l.s.common.config.resource.config;

import java.io.IOException;

public class NestedIOException extends IOException {

	public NestedIOException(String msg, Throwable cause) {
		super(msg, cause);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

}
