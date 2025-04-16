package l.s.common.httpclient.http2;

import l.s.common.httpclient.AbstractHttpClientBuilder;
import l.s.common.httpclient.support.NoConnectionReuseStrategy;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.impl.CookieSpecSupport;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.RequestAddCookies;
import org.apache.hc.client5.http.protocol.ResponseProcessCookies;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.TimeValue;

public class Http2ClientBuilder extends AbstractHttpClientBuilder {

    private HttpAsyncClientBuilder builder;

    private PoolingAsyncClientConnectionManagerBuilder poolingAsyncClientConnectionManagerBuilder;

    private CloseableHttpAsyncClient client;

    private Http2ClientBuilder(){

        defaultHttpVersion = HttpVersion.HTTP_2_0;
        poolingAsyncClientConnectionManagerBuilder = PoolingAsyncClientConnectionManagerBuilder.create()
                .useSystemProperties()
                .setMaxConnPerRoute(100)
                .setMaxConnTotal(200)
                .setTlsStrategy(createTlsStrategy())
        ;

        builder = HttpAsyncClientBuilder.create()
                .useSystemProperties()
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMinutes(1))
                .disableRedirectHandling()
                .setUserAgent("");

        init();
    }

    private Http2ClientBuilder(HttpAsyncClientBuilder builder){
        this.builder = builder;
        defaultHttpVersion = HttpVersion.HTTP_2_0;
        init();
    }

    private Http2ClientBuilder(CloseableHttpAsyncClient client){
        this.client = client;
        defaultHttpVersion = HttpVersion.HTTP_2_0;
        init();
    }

    public HttpAsyncClientBuilder getHttpAsyncClientBuilder(){
        return builder;
    }
    public PoolingAsyncClientConnectionManagerBuilder getPoolingAsyncClientConnectionManagerBuilder() {
        return poolingAsyncClientConnectionManagerBuilder;
    }

    public static Http2ClientBuilder create(){
        return new Http2ClientBuilder();
    }

    public static Http2ClientBuilder create(CloseableHttpAsyncClient client){
        return new Http2ClientBuilder(client);
    }

    public static Http2ClientBuilder create(HttpAsyncClientBuilder builder){
        return new Http2ClientBuilder(builder);
    }

    @Override
    public Http2Client build() {


        CloseableHttpAsyncClient closeableHttpAsyncClient;
        if(client != null){
            closeableHttpAsyncClient = client;
        }else {
            RegistryBuilder<CookieSpecFactory> registryBuilder = CookieSpecSupport.createDefaultBuilder();
            Registry<CookieSpecFactory> registry = registryBuilder
                    .register("RFC6265", new RFC6265CookieSpecFactory()).build();
            closeableHttpAsyncClient = builder
                    .setConnectionReuseStrategy(connectionReuseStrategy)
                    .setConnectionManager(poolingAsyncClientConnectionManagerBuilder
                            .setDefaultConnectionConfig(defaultConnectionConfig.build()).build())
                    .setDefaultRequestConfig(defaultRequestConfig.build())
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultCookieSpecRegistry(registry)
                    .addRequestInterceptorFirst(new RequestAddCookies())
                    .addResponseInterceptorFirst(new ResponseProcessCookies())
                    .setDefaultCredentialsProvider(credentialsStore)
                    .setRoutePlanner(routePlanner)
                    .setRetryStrategy(httpRequestRetryStrategy)
                    .build();
        }
        return new Http2Client(closeableHttpAsyncClient, connectTimeout, responseTimeout, defaultRequestCharset, defaultResponseCharset,
                defaultHttpVersion, defaultHeaders, cookieStore, credentialsStore, routePlanner, retryTimes);
    }
}
