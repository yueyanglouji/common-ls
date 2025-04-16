package l.s.common.httpclient.http1;

import l.s.common.httpclient.common.Response;
import l.s.common.httpclient.common.ResponseContent;
import l.s.common.httpclient.common.ResponseHeader;
import org.apache.hc.client5.http.entity.DeflateInputStream;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class ContentResponseHandle implements HttpClientResponseHandler<Response>{
	
	private final String defaultCharset;
	
	private final String charset;
	
	public ContentResponseHandle(String charset, String defaultCharset) {
		this.defaultCharset = defaultCharset;
		this.charset = charset;
	}

	@Override
	public Response handleResponse(ClassicHttpResponse r) throws IOException {
		InputStream inputStream = null;
		InputStreamReader scanner = null;
		
		try{
			Response response = new Response();
			
			ResponseHeader header = new ResponseHeader();
			try{
				Header[] hs = r.getHeaders();
				if(hs != null){
					for(int i=0;i<hs.length;i++){
						Header h = hs[i];
						header.addHeader(h.getName(), h.getValue());
					}
				}
				
			}catch(Exception e){
			}
			response.setHeader(header);
			try {
				inputStream = r.getEntity().getContent();
			} catch (Exception e) {
				inputStream = null;
			}
			
			response.getHeader().setHttpVersion(r.getVersion().toString());
			response.getHeader().setStatusCode(r.getCode() + "");
			response.getHeader().setStatusDescription(r.getReasonPhrase());
			
			String contentType = header.getHeader("Content-Type");
			
			String contentEncoding = header.getHeader("Content-Encoding");
			
			if(inputStream == null){
				ResponseContent content = new ResponseContent();
				content.setContent("");
				response.setContent(content);
			}else{
				if(charset == null || charset.equals("")){
					if(contentType!=null&&contentType.toLowerCase().indexOf("charset=")!=-1){
						contentType = contentType.substring(contentType.toLowerCase().indexOf("charset=") + 8);
						//text/html; charset=gbk;text/javascript
						if(contentType.indexOf(";") > 0){
							contentType = contentType.substring(0, contentType.indexOf(";")).trim();
						}
					}else{
						contentType = defaultCharset;
					}
				}else{
					contentType = charset;
				}
				
				if(contentEncoding!=null && contentEncoding.equalsIgnoreCase("gzip")){
					scanner = new InputStreamReader(new GZIPInputStream(inputStream), contentType);
				}
				else if(contentEncoding!=null && contentEncoding.equalsIgnoreCase("deflate")){
					scanner = new InputStreamReader(new DeflateInputStream(inputStream), contentType);
				}
				else{
					scanner = new InputStreamReader(inputStream, contentType);
				}
				
				StringBuilder builder = new StringBuilder();
				char[] ch = new char[1024];
				while(true){
					int n = scanner.read(ch);
					if(n == -1){
						break;
					}
					builder.append(ch, 0, n);
				}
				ResponseContent content = new ResponseContent();
				content.setContent(builder.toString());
				
				response.setContent(content);
			}
			
			response.setSuccess(true);
			
			return response;
			
		}catch(Throwable e){
			Response response = new Response();
			response.setSuccess(false);
			response.setException(e);
			return response;
		}finally {
			l.s.common.util.IoUtil.close(scanner);
			l.s.common.util.IoUtil.close(inputStream);
		}
		
	}

}
