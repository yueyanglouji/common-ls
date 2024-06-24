package l.s.common.httpclient;

import l.s.common.httpclient.common.RequestHeader;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.Timeout;

public abstract class HttpClient {

    private final CookieStore cookieStore;

    private final CredentialsStore credentialsStore;

    private final Timeout connectTimeout;

    private final Timeout responseTimeout;

    private final String defaultRequestCharset;

    private final String defaultResponseCharset;

    private final HttpVersion defaultHttpVersion;

    private final RequestHeader defaultHeaders;

    private final HttpRoutePlanner routePlanner;

    public HttpClient(Timeout connectTimeout, Timeout responseTimeout,
                String defaultRequestCharset, String defaultResponseCharset,
                HttpVersion defaultHttpVersion, RequestHeader defaultHeaders,
                CookieStore cookieStore, CredentialsStore credentialsStore, HttpRoutePlanner routePlanner){

        this.cookieStore = cookieStore;
        this.credentialsStore = credentialsStore;
        this.connectTimeout = connectTimeout;
        this.responseTimeout = responseTimeout;
        this.defaultRequestCharset = defaultRequestCharset;
        this.defaultResponseCharset = defaultResponseCharset;
        this.defaultHttpVersion = defaultHttpVersion;
        this.defaultHeaders = defaultHeaders;
        this.routePlanner = routePlanner;
    }

    public abstract HttpClient start();

    public abstract HttpClient awaitShutdown(Timeout timeout);

    public abstract HttpClient initiateShutdown();

    public abstract HttpClient shutdownNow();

    public RequestHeader getDefaultHeaders() {
        return defaultHeaders;
    }

    public HttpConnector connect(String url) throws Exception{
        return HttpConnector.connect(url, this);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public HttpRoutePlanner getRoutePlanner() {
        return routePlanner;
    }

    public CredentialsStore getCredentialsStore() {
        return credentialsStore;
    }

    public Timeout getConnectTimeout() {
        return connectTimeout;
    }


    public Timeout getResponseTimeout() {
        return responseTimeout;
    }

    public String getDefaultRequestCharset(){
        return defaultRequestCharset;
    }

    public String getDefaultResponseCharset(){
        return defaultResponseCharset;
    }

    public HttpVersion getHttpVersion() {
        return defaultHttpVersion;
    }

    public abstract ModalCloseable getOriginalHttpClient();

    public abstract Async getAsync();

    public abstract HttpClientType getType();
}
