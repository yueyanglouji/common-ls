package l.s.common.httpclient.http1;

import l.s.common.httpclient.HttpClientType;
import l.s.common.httpclient.common.DownloadDevice;
import l.s.common.httpclient.common.Response;
import l.s.common.httpclient.common.ResponseContent;
import l.s.common.httpclient.common.ResponseHeader;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.entity.DeflateInputStream;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class DownloadResponseHandle implements HttpClientResponseHandler<Response>{
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private OutputStream download;
	
	private DownloadDevice downloadDevice;
	
	public DownloadResponseHandle(OutputStream download) {
		this.download = download;
		downloadDevice = new DownloadDevice(HttpClientType.HTTP_1_CLIENT);
	}

	@Override
	public Response handleResponse(ClassicHttpResponse r) throws ClientProtocolException, IOException {
		InputStream inputStream = null;
		InputStream in = null;

		try{
			System.out.println("##################################################################");
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
				HttpEntity entity = r.getEntity();
				entity.getContent();
				inputStream = entity.getContent();
			} catch (Exception e) {
				inputStream = null;
			}
			
			response.getHeader().setHttpVersion(r.getVersion().toString());
			response.getHeader().setStatusCode(r.getCode() + "");
			response.getHeader().setStatusDescription(r.getReasonPhrase());
			
			String contentType = header.getHeader("Content-Type");
			
			String contentEncoding = header.getHeader("Content-Encoding");
			
			if(inputStream == null){
				download.close();
				downloadDevice.setRate(1);
				downloadDevice.setSize(0);
				ResponseContent content = new ResponseContent();
				content.setContent("");
				response.setContent(content);
			}else{
				if(contentEncoding!=null && contentEncoding.toLowerCase().equals("gzip")){
					in = new GZIPInputStream(inputStream);
				}
				else if(contentEncoding!=null && contentEncoding.toLowerCase().equals("deflate")){
					in = new DeflateInputStream(inputStream);
				}
				else{
					in = inputStream;
				}
				
				long contentLength = -1;
				String headerContentLength = header.getHeader("Content-Length");
				if(headerContentLength != null){
					if(headerContentLength.startsWith("[")){
						headerContentLength = headerContentLength.substring(1,headerContentLength.length()-1);
					}
					contentLength = Long.parseLong(headerContentLength);
				}
				
				byte[] b = new byte[1024];
				
				long length = 0;
				while(true){
					try {
						int n = in.read(b);
						if(n == -1){
							break;
						}
						download.write(b, 0, n);
						length += n;
					} catch (Exception e) {
						log.warn(e.getMessage());
						break;
					}
					
					if(contentLength == -1){
						continue;
					}
					downloadDevice.setRate((float)(length * 1.0/contentLength));
					downloadDevice.setSize(length / 1024.0F / 1024.0F);
				}
				downloadDevice.setRate(1);
				downloadDevice.setSize(length / 1024.0F / 1024.0F);
				in.close();
				inputStream.close();
				download.flush();
				download.close();
				
				ResponseContent content = new ResponseContent();
				content.setContent(contentType);
				response.setContent(content);
			}
			
			response.setSuccess(true);
			
			downloadDevice.setStatus(true);
			downloadDevice.setRate(1);
			downloadDevice.setResponse(response);
			downloadDevice.setWaite(false);
			return response;
			
		}catch(Throwable e){
			e.printStackTrace();
			if(inputStream != null){
				inputStream.close();
			}
			if(in != null){
				in.close();
			}
			Response response = new Response();
			response.setSuccess(false);
			response.setException(e);
			
			downloadDevice.setStatus(false);
			downloadDevice.setRate(-1);
			downloadDevice.setResponse(response);
			downloadDevice.setException(e);
			downloadDevice.setWaite(false);
			return response;
		}
		
	}

	public DownloadDevice getDownloadDevice() {
		return downloadDevice;
	}
}
