package l.s.common.httpclient.http2;

import l.s.common.httpclient.HttpClient;
import l.s.common.httpclient.HttpClientType;
import l.s.common.httpclient.common.RequestHeader;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Timeout;

public class Http2Client extends HttpClient {
	private final Async2 async;
	private final CloseableHttpAsyncClient client;
	
	Http2Client(CloseableHttpAsyncClient client, Timeout connectTimeout, Timeout responseTimeout,
				String defaultRequestCharset, String defaultResponseCharset,
				HttpVersion defaultHttpVersion, RequestHeader defaultHeaders,
				CookieStore cookieStore, CredentialsStore credentialsStore, HttpRoutePlanner routePlanner){
		super(connectTimeout, responseTimeout, defaultRequestCharset, defaultResponseCharset, defaultHttpVersion,
				defaultHeaders, cookieStore, credentialsStore, routePlanner);

		this.client = client;
		this.async = Async2.newInstance();
		this.async.use(client);
	}
	
	public static Http2Client getNewInstance(){
		return getNewInstance(false, Timeout.ofSeconds(4), Timeout.ofSeconds(4));
	}

	public static Http2Client getNewInstance(boolean useKeepAlive, int connectTimeout, int responseTimeout){
		return getNewInstance(useKeepAlive, Timeout.ofSeconds(connectTimeout), Timeout.ofSeconds(responseTimeout));
	}

	public static Http2Client getNewInstance(boolean useKeepAlive, Timeout connectTimeout, Timeout responseTimeout){
		return (Http2Client) Http2ClientBuilder.create().useKeepAlive(useKeepAlive)
				.connectTimeout(connectTimeout)
				.responseTimeout(responseTimeout)
				.build();
	}
	
	public static Http2Client getNewInstance(CloseableHttpAsyncClient client){
		return Http2ClientBuilder.create(client)
				.build();
	}

	@Override
	public Http2Client start(){
		this.client.start();
		return this;
	}

	@Override
	public Http2Client awaitShutdown(Timeout timeout){
		try {
			this.client.awaitShutdown(timeout);
			this.client.close(CloseMode.GRACEFUL);
		} catch (InterruptedException e) {
			// Exception
		}
		return this;
	}

	@Override
	public Http2Client initiateShutdown(){
		this.client.initiateShutdown();
		this.client.close(CloseMode.IMMEDIATE);
		return this;
	}

	@Override
	public Http2Client shutdownNow(){
		return initiateShutdown();
	}

	@Override
	public CloseableHttpAsyncClient getOriginalHttpClient() {
		return client;
	}

	@Override
	public Async2 getAsync() {
		return async;
	}

	@Override
	public HttpClientType getType() {
		return HttpClientType.HTTP_2_CLIENT;
	}
}
