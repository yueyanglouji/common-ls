package l.s.common.httpclient.http1;

import l.s.common.httpclient.HttpClient;
import l.s.common.httpclient.HttpClientType;
import l.s.common.httpclient.common.RequestHeader;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Timeout;

public class Http1Client extends HttpClient {
	private final Async1 async;

	private final CloseableHttpClient client;
	
	Http1Client(CloseableHttpClient client, Timeout connectTimeout, Timeout responseTimeout,
				String defaultRequestCharset, String defaultResponseCharset,
				HttpVersion defaultHttpVersion, RequestHeader defaultHeaders,
				CookieStore cookieStore, CredentialsStore credentialsStore, HttpRoutePlanner routePlanner){
		super(connectTimeout, responseTimeout, defaultRequestCharset, defaultResponseCharset, defaultHttpVersion,
				defaultHeaders, cookieStore, credentialsStore, routePlanner);
		this.client = client;
		this.async = Async1.newInstance();
		this.async.use(client);
	}
	
	public static Http1Client getNewInstance(){
		return getNewInstance(false, Timeout.ofSeconds(4), Timeout.ofSeconds(4));
	}

	public static Http1Client getNewInstance(boolean useKeepAlive, int connectTimeout, int responseTimeout){
		return getNewInstance(useKeepAlive, Timeout.ofSeconds(connectTimeout), Timeout.ofSeconds(responseTimeout));
	}

	public static Http1Client getNewInstance(boolean useKeepAlive, Timeout connectTimeout, Timeout responseTimeout){
		return (Http1Client) Http1ClientBuilder.create().useKeepAlive(useKeepAlive)
				.connectTimeout(connectTimeout)
				.responseTimeout(responseTimeout)
				.build();
	}
	
	public static Http1Client getNewInstance(CloseableHttpClient client){
		return Http1ClientBuilder.create(client)
				.build();
	}

	@Override
	public Http1Client start(){
		return this;
	}

	@Override
	public Http1Client awaitShutdown(Timeout timeout){
		this.client.close(CloseMode.GRACEFUL);
		return this;
	}

	@Override
	public Http1Client initiateShutdown(){
		this.client.close(CloseMode.IMMEDIATE);
		return this;
	}

	@Override
	public Http1Client shutdownNow(){
		return initiateShutdown();
	}

	@Override
	public CloseableHttpClient getOriginalHttpClient() {
		return client;
	}

	@Override
	public Async1 getAsync() {
		return async;
	}

	@Override
	public HttpClientType getType() {
		return HttpClientType.HTTP_1_CLIENT;
	}
}
