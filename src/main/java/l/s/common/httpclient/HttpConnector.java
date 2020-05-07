package l.s.common.httpclient;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;


public class HttpConnector {
	
	private Request request;
	
	private AsyncHttpClient client;
	
	private Timeout connectTimeout;
	
	private String proxyHost = null;
	
	private int proxyPort = 80;
	
	private String proxyScheme = "http";
	
	private String requestCharset;

	private String responseCharset;
	
	private ContentType contentType;
	
	private HttpConnector(){
	}
	
	public static HttpConnector connect(String url, AsyncHttpClient client) throws Exception{
		HttpConnector connect = new HttpConnector();
		connect.request = new Request(new URL(url));
		
		connect.client = client;
		
		connect.connectTimeout = client.getConnectTimeout();
		
		return connect;
	}
	
	public HttpConnector connectTimeout(Timeout connectTimeout){
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	public HttpConnector viaProxy(String proxyHost, int proxyPort){
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		return this;
	}
	
	public HttpConnector viaProxy(String proxyScheme, String proxyHost, int proxyPort){
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyScheme = proxyScheme;
		return this;
	}
	
	public HttpConnector useRequestCharset(String requestCharset){
		this.requestCharset = requestCharset;
		return this;
	}
	
	public HttpConnector useResponseCharset(String responseCharset){
		this.responseCharset = responseCharset;
		return this;
	}
	
	public HttpConnector header(String name, String value){
		request.addHeader(name, value);
		return this;
	}
	
	public HttpConnector param(String name, String value){
		request.addParam(name, value);
		return this;
	}
	
	public HttpConnector stream(String text) throws Exception{
		request.stream(text);
		return this;
	}
	
	public HttpConnector stream(InputStream in) throws Exception{
		request.stream(in);
		return this;
	}
	
	public HttpConnector contentType(ContentType contentType){
		this.contentType = contentType;
		return this;
	}
	
	public Future<Response> getAsync() throws Exception{
		return getAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	public Response get() throws Exception{
		Future<Response> f = getAsync();
		return f.get();
	}
	
	public Future<Response> postAsync() throws Exception{
		return postAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	public Response post() throws Exception{
		Future<Response> f = postAsync();
		return f.get();
	}
	
	public Future<Response> putAsync() throws Exception{
		return putAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	public Response put() throws Exception{
		Future<Response> f = putAsync();
		return f.get();
	}
	
	public Future<Response> deleteAsync() throws Exception{
		return deleteAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	public Response delete() throws Exception{
		Future<Response> f = deleteAsync();
		return f.get();
	}
	
	public<T> Future<T> getAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.GET);
		
		org.apache.hc.client5.http.fluent.Request r = toHttpClientRequest();
		
		Future<T> f = client.getAsync().execute(r, handler);
		return f;
	}
	
	public<T> T get(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = getAsync(handler);
		return f.get();
	}
	
	public<T> Future<T> postAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.POST);
		
		org.apache.hc.client5.http.fluent.Request r = toHttpClientRequest();
		
		Future<T> f = client.getAsync().execute(r, handler);
		return f;
	}
	
	public<T> T post(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = postAsync(handler);
		return f.get();
	}
	
	public<T> Future<T> putAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.PUT);
		
		org.apache.hc.client5.http.fluent.Request r = toHttpClientRequest();
		
		Future<T> f = client.getAsync().execute(r, handler);
		return f;
	}
	
	public<T> T put(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = putAsync(handler);
		return f.get();
	}
	
	public<T> Future<T> deleteAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.DELETE);
		
		org.apache.hc.client5.http.fluent.Request r = toHttpClientRequest();
		
		Future<T> f = client.getAsync().execute(r, handler);
		return f;
	}
	
	public<T> T delete(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = deleteAsync(handler);
		return f.get();
	}
	
	public DownloadDevice downloadGet(OutputStream out) throws Exception{
		request.setMethod(RequestMethod.GET);
		
		org.apache.hc.client5.http.fluent.Request r = toHttpClientRequest();

		DownloadResponseHandle handle = new DownloadResponseHandle(out);
		Future<?> f = client.getAsync().execute(r, handle);
		handle.doRequest(f);
		return handle.getDownloadDevice();
	}
	
	public DownloadDevice downloadPost(OutputStream out) throws Exception{
		request.setMethod(RequestMethod.POST);
		
		org.apache.hc.client5.http.fluent.Request r = toHttpClientRequest();

		DownloadResponseHandle handle = new DownloadResponseHandle(out);
		Future<?> f = client.getAsync().execute(r, handle);
		handle.doRequest(f);
		return handle.getDownloadDevice();
	}
	
	private org.apache.hc.client5.http.fluent.Request toHttpClientRequest() throws Exception{
	
		String url = request.getUrl().toString();
		org.apache.hc.client5.http.fluent.Request client;
		
		if(request.getMethod() != null && request.getMethod() == RequestMethod.GET){
			setGetParam();
		}else{
			setPostParam();
		}
		
		if(request.getMethod() != null){
			if(request.getMethod() == RequestMethod.GET){
				client = org.apache.hc.client5.http.fluent.Request.get(url);
			}
			else if(request.getMethod() == RequestMethod.POST){
				client = org.apache.hc.client5.http.fluent.Request.post(url);
			}
			else if(request.getMethod() == RequestMethod.PUT){
				client = org.apache.hc.client5.http.fluent.Request.put(url);
			}
			else if(request.getMethod() == RequestMethod.DELETE){
				client = org.apache.hc.client5.http.fluent.Request.delete(url);
			}
			else{
				client = org.apache.hc.client5.http.fluent.Request.post(url);
			}
		}else{
			client = org.apache.hc.client5.http.fluent.Request.post(url);
		}

		Timeout connOut = this.connectTimeout != null? this.connectTimeout : this.client.getConnectTimeout();
		
		client.connectTimeout(connOut.get());
		
		if(this.proxyHost != null && !this.proxyHost.equals("")){
			client.viaProxy(new HttpHost(proxyScheme, proxyHost, proxyPort));
		}
		else if(this.client.getProxyHost() != null){
			client.viaProxy(new HttpHost( this.client.getProxyScheme(), this.client.getProxyHost(), this.client.getProxyPort()));
		}
		
		Map<String , String> headers = request.getHeaders();
		Map<String , String> lowercaseHeaders = request.getHeadersWithLowercaseKey();
		for(Entry<String, String> e:headers.entrySet()){
			client.addHeader(e.getKey(), e.getValue());
		}
		
		if(lowercaseHeaders.get("cache-control") == null){
			client.setCacheControl("no-cache");
		}
		
		if(lowercaseHeaders.get("content-type") == null){
			if(this.contentType != null){
				client.addHeader("Content-Type", this.contentType.getMimeType());
			}
			else if(this.request.getMethod() != RequestMethod.GET){
				if(this.request.getParam().getParam().size() > 0){
					client.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
				}else{
					client.addHeader("Content-Type", ContentType.DEFAULT_BINARY.getMimeType());
				}
			}
		}
		
		HttpUpInputPutStream stream = request.getUpStream();
		if(stream.size() > 0){
			String charset = this.requestCharset == null?this.client.getDefaultRequestCharset() : this.requestCharset;
			HttpUpStreamEntity entity = new HttpUpStreamEntity(ContentType.DEFAULT_BINARY, charset);
			stream.write(entity, charset);
			client.body(entity);
		}
		
		return client;
		
	}

	private void setGetParam() throws Exception{
		String url = request.getUrl().toString();
		String p = getParams();
		
		if(p != null && !p.equals("")){
			if(url.indexOf('?')==-1){
				url += "?";
			}
			else if(url.charAt(url.length() - 1) != '?' && url.charAt(url.length() - 1) != '&'){
				url += "&";
			}
			
			url += p;
			request.setUrl(new URL(url));
		}
	}
	
	private void setPostParam() throws Exception{
		String p = getParams();
		
		if(p != null && !p.equals("")){
			request.getUpStream().addFirst(p);
		}
	}
	
	private String getParams() throws UnsupportedEncodingException{
		Map<String, List<String>> params = request.getParam().getParam();
		
		StringBuilder builder = new StringBuilder();
		int n = 0;
		for(Entry<String, List<String>> e: params.entrySet()){
			if(n != 0){
				builder.append("&");
			}
			for(int i=0;i<e.getValue().size();i++){
				builder.append(e.getKey() + "=");
				String value = e.getValue().get(i);
				if(value == null) value = "";
				builder.append(URLEncoder.encode(value, "UTF-8"));
				
				if(i != e.getValue().size()-1){
					builder.append("&");
				}
			}
			n++;
		}
		
		return builder.toString();
	}
}
