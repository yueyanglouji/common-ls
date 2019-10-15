package l.s.common.httpclient;

import org.apache.hc.client5.http.cookie.CookieSpecs;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.TimeValue;

import l.s.common.httpclient.support.CustomClientConnectionReuseStrategy;
import l.s.common.httpclient.support.NoConnectionReuseStrategy;

public class AsyncHttpClient {
	
	final static CloseableHttpClient CLIENT_NO_KEEP;
	
	final static CloseableHttpClient CLIENT_KEEP_ALIVE;
	
	static {
        	RequestConfig globalConfig = RequestConfig.custom()
        			.setCookieSpec(CookieSpecs.STANDARD.ident)
        			.setConnectTimeout(Timeout.ofSeconds(4).get())
        			//.setSocketTimeout(Timeout.ofSeconds(4))
        			.setConnectionRequestTimeout(Timeout.ofSeconds(4).get())
        			.setRedirectsEnabled(false)
        			.build();
        
			CLIENT_KEEP_ALIVE = HttpClientBuilder.create()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .useSystemProperties()
                        .setMaxConnPerRoute(100)
                        .setMaxConnTotal(200)
                        .setValidateAfterInactivity(TimeValue.ofSeconds(10))
                        .build())
                .useSystemProperties()
                .setDefaultRequestConfig(globalConfig)
                .setConnectionReuseStrategy(new CustomClientConnectionReuseStrategy())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMinutes(1))
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .build();
			
			CLIENT_NO_KEEP = HttpClientBuilder.create()
	                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
	                        .useSystemProperties()
	                        .setMaxConnPerRoute(100)
	                        .setMaxConnTotal(200)
	                        .setValidateAfterInactivity(TimeValue.ofSeconds(10))
	                        .build())
	                .useSystemProperties()
	                .setDefaultRequestConfig(globalConfig)
	                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
	                .evictExpiredConnections()
	                .evictIdleConnections(TimeValue.ofMinutes(1))
	                .disableAutomaticRetries()
	                .disableConnectionState()
	                .disableRedirectHandling()
	                .build();
	}
	
	private Async async;
	
	private Executor executor;
	
	private CloseableHttpClient httpClient;
	
	private CookieStore cookieStore;
	
	private Timeout connecttimeout;
	
	private String proxyHost;
	
	private String proxyScheme = "http";
	
	private int proxyPort;
	
	private String proxyUsername;
	
	private String proxyPassword;
	
	private String defaultRequestCharset;
	
	private String defaultResponseCharset;
	
	private AsyncHttpClient(CloseableHttpClient client){
		this.httpClient = client;
		
		this.executor = Executor.newInstance(httpClient);
		this.cookieStore = new CookieStore();
		this.executor.use(cookieStore);
		
		this.async = Async.newInstance();
		this.async.use(this.executor);
		this.connecttimeout = Timeout.ofSeconds(4);
		this.defaultRequestCharset = "UTF-8";
		this.defaultResponseCharset = "UTF-8";
	}
	
	public static AsyncHttpClient getNewInstance(){
		return getNewInstance(false);
	}
	
	public static AsyncHttpClient getNewInstance(boolean useKeepAlive){
		AsyncHttpClient r;
		if(useKeepAlive){
			r = new AsyncHttpClient(CLIENT_KEEP_ALIVE);
		}else{
			r = new AsyncHttpClient(CLIENT_NO_KEEP);
		}
		return r;
	}
	
	public static AsyncHttpClient getNewInstance(CloseableHttpClient client){
		AsyncHttpClient r = new AsyncHttpClient(client);
		return r;
	}
	
	public AsyncHttpClient connectTimenout(org.apache.hc.core5.util.Timeout connecttimeout){
		this.connecttimeout = Timeout.ofTimeout(connecttimeout);
		return this;
	}
	
	public AsyncHttpClient connectTimenout(Timeout connecttimeout){
		this.connecttimeout = connecttimeout;
		return this;
	}
	
	public AsyncHttpClient proxy(String proxyHost, int proxyPort){
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		return this;
	}
	
	public AsyncHttpClient proxy(String proxyHost, int proxyPort, String username, String password){
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUsername = username;
		this.proxyPassword = password;
		
		executor.auth(new HttpHost(proxyHost, proxyPort), username, password.toCharArray());
		return this;
	}
	
	public AsyncHttpClient proxy(String proxyHost, int proxyPort, String proxyScheme){
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyScheme = proxyScheme;
		return this;
	}
	
	public AsyncHttpClient proxy(String proxyScheme, String proxyHost, int proxyPort, String username, String password){
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyScheme = proxyScheme;
		this.proxyUsername = username;
		this.proxyPassword = password;
		
		executor.auth(new HttpHost(proxyScheme, proxyHost, proxyPort), username, password.toCharArray());
		return this;
	}
	
	public AsyncHttpClient useDefaultRequestCharset(String defaultRequestCharset){
		this.defaultRequestCharset = defaultRequestCharset;
		return this;
	}
	
	public AsyncHttpClient useDefaultResponseCharset(String defaultResponseCharset){
		this.defaultResponseCharset = defaultResponseCharset;
		return this;
	}
	
	public AsyncHttpClient useCookeStore(boolean b){
		if(!b){
			this.executor.use(new NoCookieStore());
		}else{
			this.executor.use(cookieStore);
		}
		
		return this;
	}
	
	public HttpConnector connect(String url) throws Exception{
		return HttpConnector.connect(url, this);
	}
	
	public Async getAsync() {
		return async;
	}

	public Executor getExecutor() {
		return executor;
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public Timeout getConnecttimeout() {
		return connecttimeout;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getProxyScheme() {
		return proxyScheme;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}
	
	public String getDefaultRequestCharset(){
		return defaultRequestCharset;
	}
	
	public String getDefaultResponseCharset(){
		return defaultResponseCharset;
	}
	
}
