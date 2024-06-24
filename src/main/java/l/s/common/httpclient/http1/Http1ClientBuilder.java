package l.s.common.httpclient.http1;

import l.s.common.httpclient.AbstractHttpClientBuilder;
import l.s.common.httpclient.support.NoConnectionReuseStrategy;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.impl.CookieSpecSupport;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.RequestAddCookies;
import org.apache.hc.client5.http.protocol.ResponseProcessCookies;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.TimeValue;

public class Http1ClientBuilder extends AbstractHttpClientBuilder {

    private HttpClientBuilder builder;
    private CloseableHttpClient client;

    private PoolingHttpClientConnectionManagerBuilder poolingHttpClientConnectionManagerBuilder;

    private Http1ClientBuilder() {
        super();
        defaultHttpVersion = HttpVersion.HTTP_1_1;
        poolingHttpClientConnectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create()
                .useSystemProperties()
                .setMaxConnPerRoute(100)
                .setMaxConnTotal(200)
                .setSSLSocketFactory(createSSLConnectionSocketFactory())
        ;
        builder = HttpClientBuilder.create()
                .useSystemProperties()
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMinutes(1))
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .disableDefaultUserAgent();
        init();
    }

    private Http1ClientBuilder(HttpClientBuilder builder){
        this.builder = builder;
        defaultHttpVersion = HttpVersion.HTTP_1_1;
        init();
    }

    private Http1ClientBuilder(CloseableHttpClient client){
        this.client = client;
        defaultHttpVersion = HttpVersion.HTTP_1_1;
        init();
    }

    public HttpClientBuilder getHttpClientBuilder(){
        return builder;
    }

    public static Http1ClientBuilder create(){
        return new Http1ClientBuilder();
    }

    public static Http1ClientBuilder create(CloseableHttpClient client){
        return new Http1ClientBuilder(client);
    }

    public static Http1ClientBuilder create(HttpClientBuilder builder){
        return new Http1ClientBuilder(builder);
    }


    @Override
    public Http1Client build() {


        CloseableHttpClient closeableHttpClient;
        if(client != null){
            closeableHttpClient = client;
        }else {
            RegistryBuilder<CookieSpecFactory> registryBuilder = CookieSpecSupport.createDefaultBuilder();
            Registry<CookieSpecFactory> registry = registryBuilder
                    .register("RFC6265", new RFC6265CookieSpecFactory()).build();
            closeableHttpClient = builder
                    .setConnectionReuseStrategy(connectionReuseStrategy)
                    .setConnectionManager(poolingHttpClientConnectionManagerBuilder
                            .setDefaultConnectionConfig(defaultConnectionConfig.build()).build())
                    .setDefaultRequestConfig(defaultRequestConfig.build())
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultCookieSpecRegistry(registry)
                    .addRequestInterceptorFirst(new RequestAddCookies())
                    .addResponseInterceptorFirst(new ResponseProcessCookies())
                    .setDefaultCredentialsProvider(credentialsStore)
                    .setRoutePlanner(routePlanner)
                    .build();
        }
        return new Http1Client(closeableHttpClient, connectTimeout, responseTimeout,
                defaultRequestCharset, defaultResponseCharset,
                defaultHttpVersion, defaultHeaders, cookieStore,
                credentialsStore, routePlanner);
    }
}
