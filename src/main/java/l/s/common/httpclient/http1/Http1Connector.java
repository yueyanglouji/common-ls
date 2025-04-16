package l.s.common.httpclient.http1;

import l.s.common.httpclient.HttpClient;
import l.s.common.httpclient.HttpConnector;
import l.s.common.httpclient.common.DownloadDevice;
import l.s.common.httpclient.common.Request;
import l.s.common.httpclient.common.RequestMethod;
import l.s.common.httpclient.common.Response;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Http1Connector extends HttpConnector {

	private Http1Connector(){
	}
	
	public static Http1Connector doConnect(String url, HttpClient client) throws Exception{
		Http1Connector connect = new Http1Connector();
		connect.request = new Request(new URL(url), client.getDefaultHeaders());
		
		connect.client = client;
		
		connect.connectTimeout = client.getConnectTimeout();

		connect.httpVersion = client.getHttpVersion();
		
		return connect;
	}
	
	@Override
	public Future<Response> getAsync() throws Exception{
		return getAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	@Override
	public Future<Response> postAsync() throws Exception{
		return postAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	@Override
	public Future<Response> putAsync() throws Exception{
		return putAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	@Override
	public Future<Response> deleteAsync() throws Exception{
		return deleteAsync(new ContentResponseHandle(this.responseCharset, client.getDefaultResponseCharset()));
	}
	
	public<T> Future<T> getAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.GET);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(r, localeContext, handler);
	}
	
	public<T> T get(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = getAsync(handler);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}
	
	public<T> Future<T> postAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.POST);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(r, localeContext, handler);
	}
	
	public<T> T post(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = postAsync(handler);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}
	
	public<T> Future<T> putAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.PUT);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(r, localeContext, handler);
	}
	
	public<T> T put(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = putAsync(handler);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}
	
	public<T> Future<T> deleteAsync(HttpClientResponseHandler<T> handler) throws Exception{
		request.setMethod(RequestMethod.DELETE);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(r, localeContext, handler);
	}
	
	public<T> T delete(HttpClientResponseHandler<T> handler) throws Exception{
		Future<T> f = deleteAsync(handler);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}

	@Override
	protected DownloadDevice doDownload(OutputStream out) throws Exception {
		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		DownloadResponseHandle handle = new DownloadResponseHandle(out);
		client.getAsync().execute(r, localeContext,  handle);
		return handle.getDownloadDevice();
	}
}
