package l.s.common.httpclient.common;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;
import org.apache.hc.core5.util.Args;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpUpStreamEntity extends AbstractHttpEntity implements Cloneable {

//	private List<Object> sourceList;
	
	private ByteArrayOutputStream content;
	
	public HttpUpStreamEntity(ContentType contentType, String charset) {
		super(contentType, charset);
		this.content = new ByteArrayOutputStream();
//		setContentType("application/x-www-form-urlencoded");
	}
	
	public void add(String text, String charset)throws Exception{
		content.write(text.getBytes(charset));
	}
	
	public void add(InputStream stream) throws Exception{
		byte[] b = new byte[1024];
		
		int n = -1;
		while((n = stream.read(b)) != -1){
			content.write(b, 0, n);
		}
		stream.close();
	}
	
	public int size(){
		return content.toByteArray().length;
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public long getContentLength() {
		return content.toByteArray().length;
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		byte[] b = content.toByteArray();
		return new ByteArrayInputStream(b);
	}

	@Override
	public void writeTo(OutputStream outStream) throws IOException {
		Args.notNull(outStream, "Output stream");
		byte[] b = content.toByteArray();
				
		try {
			outStream.write(b);
			outStream.flush();
		} finally{
			content.close();
		}
		
	}

	@Override
	public void close() throws IOException {
		this.content.close();
	}

	@Override
	public boolean isStreaming() {
		return false;
	}
	
	
	
	
}
