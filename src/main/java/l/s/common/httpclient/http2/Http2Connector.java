package l.s.common.httpclient.http2;

import l.s.common.httpclient.HttpClient;
import l.s.common.httpclient.HttpConnector;
import l.s.common.httpclient.common.DownloadDevice;
import l.s.common.httpclient.common.Request;
import l.s.common.httpclient.common.RequestMethod;
import l.s.common.httpclient.common.Response;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.entity.AsyncEntityProducers;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;

import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Http2Connector extends HttpConnector {
	
	private Http2Connector(){
	}
	
	public static Http2Connector doConnect(String url, HttpClient client) throws Exception{
		Http2Connector connect = new Http2Connector();
		connect.request = new Request(new URL(url), client.getDefaultHeaders());
		
		connect.client = client;
		
		connect.connectTimeout = client.getConnectTimeout();

		connect.responseTimeout = client.getResponseTimeout();

		connect.httpVersion = client.getHttpVersion();
		
		return connect;
	}
	
	@Override
	public Future<Response> getAsync() throws Exception{
		return getAsync(toContentHandleResponseConsumer());
	}
	
	@Override
	public Future<Response> postAsync() throws Exception{
		return postAsync(toContentHandleResponseConsumer());
	}
	
	@Override
	public Future<Response> putAsync() throws Exception{
		return putAsync(toContentHandleResponseConsumer());
	}

	@Override
	public Future<Response> deleteAsync() throws Exception{
		return deleteAsync(toContentHandleResponseConsumer());
	}
	
	public<T> Future<T> getAsync(AsyncResponseConsumer<T> consumer) throws Exception{
		request.setMethod(RequestMethod.GET);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(new BasicRequestProducer(r, toAsyncEntityProducer()), consumer, localeContext);
	}
	
	public<T> T get(AsyncResponseConsumer<T> consumer) throws Exception{
		Future<T> f = getAsync(consumer);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}
	
	public<T> Future<T> postAsync(AsyncResponseConsumer<T> consumer) throws Exception{
		request.setMethod(RequestMethod.POST);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(new BasicRequestProducer(r, toAsyncEntityProducer()), consumer, localeContext);
	}
	
	public<T> T post(AsyncResponseConsumer<T> consumer) throws Exception{
		Future<T> f = postAsync(consumer);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}
	
	public<T> Future<T> putAsync(AsyncResponseConsumer<T> consumer) throws Exception{
		request.setMethod(RequestMethod.PUT);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(new BasicRequestProducer(r, toAsyncEntityProducer()), consumer, localeContext);
	}
	
	public<T> T put(AsyncResponseConsumer<T> consumer) throws Exception{
		Future<T> f = putAsync(consumer);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}
	
	public<T> Future<T> deleteAsync(AsyncResponseConsumer<T> consumer) throws Exception{
		request.setMethod(RequestMethod.DELETE);

		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		return client.getAsync().execute(new BasicRequestProducer(r, toAsyncEntityProducer()), consumer, localeContext);
	}
	
	public<T> T delete(AsyncResponseConsumer<T> consumer) throws Exception{
		Future<T> f = deleteAsync(consumer);
		return f.get(getTimeoutSeconds(), TimeUnit.SECONDS);
	}

	@Override
	protected DownloadDevice doDownload(OutputStream out) throws Exception {
		ClassicHttpRequest r = toHttpClientRequest();
		HttpClientContext localeContext = toHttpClientContext();

		DownloadResponseAsyncEntityConsumer<Response> downloadConsumer = new DownloadResponseAsyncEntityConsumer<>(out);
		HandleResponseConsumer<Response> consumer = new HandleResponseConsumer<Response>(downloadConsumer);
		client.getAsync().execute(new BasicRequestProducer(r, toAsyncEntityProducer()), consumer, localeContext);
		return downloadConsumer.getDownloadDevice();
	}

	private<T> HandleResponseConsumer<T> toContentHandleResponseConsumer(){
		return new HandleResponseConsumer<T>(new ContentResponseAsyncEntityConsumer<>(this.responseCharset, client.getDefaultResponseCharset()));
	}

	private AsyncEntityProducer toAsyncEntityProducer(){
		return AsyncEntityProducers.create("stuff", ContentType.TEXT_XML);
	}
}
