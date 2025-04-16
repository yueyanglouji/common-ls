package l.s.common.httpclient;

import l.s.common.httpclient.common.CookieStore;
import l.s.common.httpclient.common.NoCookieStore;
import l.s.common.httpclient.common.RequestHeader;
import l.s.common.httpclient.support.CustomHttpRequestRetryStrategy;
import l.s.common.httpclient.support.CustomClientConnectionReuseStrategy;
import l.s.common.httpclient.support.NoConnectionReuseStrategy;
import l.s.common.util.StringUtil;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;

public abstract class AbstractHttpClientBuilder {

    protected RequestConfig.Builder defaultRequestConfig;

    protected ConnectionConfig.Builder defaultConnectionConfig;

    protected Timeout connectTimeout;

    protected Timeout responseTimeout;

    protected String proxyHost;

    protected String proxyScheme = "http";

    protected int proxyPort;

    protected String proxyUsername;

    protected String proxyPassword;

    protected String defaultRequestCharset;

    protected String defaultResponseCharset;

    protected HttpVersion defaultHttpVersion = HttpVersion.HTTP_1_1;

    protected RequestHeader defaultHeaders;

    protected org.apache.hc.client5.http.cookie.CookieStore cookieStore;

    protected CredentialsStore credentialsStore;

    protected HttpRoutePlanner routePlanner;

    protected ConnectionReuseStrategy connectionReuseStrategy;

    protected HttpRequestRetryStrategy httpRequestRetryStrategy;

    protected int retryTimes = 0;

    protected AbstractHttpClientBuilder(){
        defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec("RFC6265")
                .setConnectionRequestTimeout(Timeout.ofSeconds(4))
                .setResponseTimeout(Timeout.ofSeconds(4))
                .setRedirectsEnabled(false);

        defaultConnectionConfig = ConnectionConfig.custom()
                .setValidateAfterInactivity(TimeValue.ofSeconds(10))
                .setConnectTimeout(Timeout.ofSeconds(4))
                .setSocketTimeout(Timeout.ofSeconds(4));
    }

    protected SSLContext createSSLContext(){
        try {
            return SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustAllStrategy())
                    .build();
        }catch (Throwable e){
            // Nothing
        }
        return null;
    }

    protected SSLConnectionSocketFactory createSSLConnectionSocketFactory(){
        try{
            return new SSLConnectionSocketFactory(createSSLContext());
        }catch (Throwable e){
            // nothing
        }
        return null;
    }

    protected TlsStrategy createTlsStrategy(){
        try{
            final SSLContext sslcontext = createSSLContext();
            return ClientTlsStrategyBuilder
                    .create()
                    .useSystemProperties()
                    .setSslContext(sslcontext)
                    .build();
        }catch (Throwable e){
            // nothing
        }
        return null;
    }

    protected void init(){
        this.connectTimeout = Timeout.ofSeconds(4);
        this.responseTimeout = Timeout.ofSeconds(4);
        this.defaultRequestCharset = "UTF-8";
        this.defaultResponseCharset = "UTF-8";
        this.cookieStore = new CookieStore();
        this.credentialsStore = new BasicCredentialsProvider();
        this.defaultHeaders = new RequestHeader();
        this.httpRequestRetryStrategy = new CustomHttpRequestRetryStrategy(0, TimeValue.ofSeconds(1L));
    }

    public RequestConfig.Builder getDefaultRequestConfig() {
        return defaultRequestConfig;
    }

    public ConnectionConfig.Builder getDefaultConnectionConfig() {
        return defaultConnectionConfig;
    }

    public AbstractHttpClientBuilder useKeepAlive(boolean b){
        if(b){
            this.connectionReuseStrategy = new CustomClientConnectionReuseStrategy();
        }else {
            this.connectionReuseStrategy = new NoConnectionReuseStrategy();
        }
        return this;
    }
    public AbstractHttpClientBuilder useCookeStore(boolean b){
        if(!b){
            this.cookieStore = new NoCookieStore();
        }else{
            this.cookieStore = new CookieStore();
        }
        return this;
    }

    public AbstractHttpClientBuilder retryTimes(int times){
        if(times > 0){
            if(times > 5){
                // max retry times is 5
                times = 5;
            }
            this.retryTimes = times;
            this.httpRequestRetryStrategy = new CustomHttpRequestRetryStrategy(times, TimeValue.ofSeconds(1L));
        }else{
            this.httpRequestRetryStrategy = new CustomHttpRequestRetryStrategy(0, TimeValue.ofSeconds(1L));
        }
        return this;
    }

    public AbstractHttpClientBuilder useCredentialsStore(CredentialsStore credentialsStore){
        this.credentialsStore = credentialsStore;
        return this;
    }

    public AbstractHttpClientBuilder addCredentials(final AuthScope authScope, final Credentials credentials){
        this.credentialsStore.setCredentials(authScope, credentials);
        return this;
    }

    public AbstractHttpClientBuilder connectTimeout(Timeout connectTimeout){
        this.connectTimeout = connectTimeout;
        return this;
    }

    public AbstractHttpClientBuilder responseTimeout(Timeout responseTimeout){
        this.responseTimeout = responseTimeout;
        return this;
    }

    public AbstractHttpClientBuilder connectTimeout(int connectTimeout){
        return connectTimeout(Timeout.ofSeconds(connectTimeout));
    }

    public AbstractHttpClientBuilder responseTimeout(int responseTimeout){
        return responseTimeout(Timeout.ofSeconds(responseTimeout));
    }

    public AbstractHttpClientBuilder proxy(String proxyHost, int proxyPort){
        return proxy(proxyScheme, proxyHost, proxyPort);
    }

    public AbstractHttpClientBuilder proxy(String proxyHost, int proxyPort, String username, String password){
        return proxy(proxyScheme, proxyHost, proxyPort, username, password);
    }

    public AbstractHttpClientBuilder proxy(String proxyScheme, String proxyHost, int proxyPort){
        proxy(proxyScheme, proxyHost, proxyPort, null, null);
        return this;
    }

    public AbstractHttpClientBuilder proxy(String proxyScheme, String proxyHost, int proxyPort, String username, String password){
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyScheme = proxyScheme;
        this.routePlanner = new DefaultProxyRoutePlanner(new HttpHost(proxyScheme, proxyHost, proxyPort));
        this.proxyUsername = username;
        this.proxyPassword = password;
        if(StringUtil.notEmpty(username) && StringUtil.notEmpty(password)){
            this.addCredentials(new AuthScope(new HttpHost(proxyScheme, proxyHost, proxyPort)), new UsernamePasswordCredentials(proxyUsername, proxyPassword.toCharArray()));
        }
        return this;
    }

    public AbstractHttpClientBuilder useDefaultRequestCharset(String defaultRequestCharset){
        this.defaultRequestCharset = defaultRequestCharset;
        return this;
    }

    public AbstractHttpClientBuilder useDefaultResponseCharset(String defaultResponseCharset){
        this.defaultResponseCharset = defaultResponseCharset;
        return this;
    }

    public AbstractHttpClientBuilder useDefaultHttpVersion(HttpVersion version){
        this.defaultHttpVersion = version;
        return this;
    }

    public AbstractHttpClientBuilder useDefaultHeader(String key, String value){
        this.defaultHeaders.addHeader(key, value);
        return this;
    }

    public abstract HttpClient build();
}
