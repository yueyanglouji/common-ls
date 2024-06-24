package l.s.common.httpclient;

import l.s.common.httpclient.common.DownloadDevice;
import l.s.common.httpclient.common.HttpUpInputPutStream;
import l.s.common.httpclient.common.HttpUpStreamEntity;
import l.s.common.httpclient.common.Request;
import l.s.common.httpclient.common.RequestMethod;
import l.s.common.httpclient.common.Response;
import l.s.common.httpclient.http1.Http1Connector;
import l.s.common.httpclient.http2.Http2Connector;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.util.Timeout;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public abstract class HttpConnector {


    protected Request request;

    protected HttpClient client;

    protected Timeout connectTimeout;

    protected Timeout responseTimeout;

    protected String proxyHost = null;

    protected int proxyPort = 80;

    protected String proxyScheme = "http";

    protected String requestCharset;

    protected String responseCharset;

    protected ContentType contentType;

    protected HttpVersion httpVersion;

    protected HttpConnector(){
    }

    public static HttpConnector connect(String url, HttpClient client) throws Exception{
        if(client.getType() == HttpClientType.HTTP_2_CLIENT){
            return Http2Connector.doConnect(url, client);
        }else {
            return Http1Connector.doConnect(url, client);
        }
    }

    public HttpConnector httpVersion(HttpVersion version){
        this.httpVersion = version;
        return this;
    }

    public HttpConnector connectTimeout(Timeout connectTimeout){
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpConnector responseTimeout(Timeout responseTimeout){
        this.responseTimeout = responseTimeout;
        return this;
    }

    public HttpConnector connectTimeout(int connectTimeout){
        return connectTimeout(Timeout.ofSeconds(connectTimeout));
    }

    public HttpConnector responseTimeout(int responseTimeout){
        return responseTimeout(Timeout.ofSeconds(responseTimeout));
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

    public abstract Future<Response> getAsync() throws Exception;

    public Response get() throws Exception{
        Future<Response> f = getAsync();
        return f.get();
    }

    public abstract Future<Response> postAsync() throws Exception;

    public Response post() throws Exception{
        Future<Response> f = postAsync();
        return f.get();
    }

    public abstract Future<Response> putAsync() throws Exception;

    public Response put() throws Exception{
        Future<Response> f = putAsync();
        return f.get();
    }

    public abstract Future<Response> deleteAsync() throws Exception;

    public Response delete() throws Exception{
        Future<Response> f = deleteAsync();
        return f.get();
    }

    public DownloadDevice downloadGet(OutputStream out) throws Exception{
        request.setMethod(RequestMethod.GET);

        return doDownload(out);
    }

    protected abstract DownloadDevice doDownload(OutputStream out) throws Exception;

    public DownloadDevice downloadPost(OutputStream out) throws Exception{
        request.setMethod(RequestMethod.POST);

        return doDownload(out);
    }

    protected HttpClientContext toHttpClientContext(){
        HttpClientContext httpClientContext = HttpClientContext.create();

        final RequestConfig.Builder builder;
        if (client.getOriginalHttpClient() instanceof Configurable) {
            builder = RequestConfig.copy(((Configurable) client.getOriginalHttpClient()).getConfig());
        } else {
            builder = RequestConfig.custom();
        }
        Timeout connOut = this.connectTimeout != null? this.connectTimeout : this.client.getConnectTimeout();
        builder.setConnectionRequestTimeout(connOut);

        Timeout responseOut = this.responseTimeout != null? this.responseTimeout : this.client.getResponseTimeout();
        builder.setResponseTimeout(responseOut);

        if(this.proxyHost != null && !"".equals(this.proxyHost)){
            builder.setProxy(new HttpHost(proxyScheme, proxyHost, proxyPort));
        }
        httpClientContext.setRequestConfig(builder.build());
        return httpClientContext;
    }

    protected BasicClassicHttpRequest toHttpClientRequest() throws Exception{

        String url = request.getUrl().toString();
        BasicClassicHttpRequest client;

        if(request.getMethod() != null && request.getMethod() == RequestMethod.GET){
            setGetParam();
        }else{
            setPostParam();
        }

        if(request.getMethod() != null){
            if(request.getMethod() == RequestMethod.GET){
                client = new BasicClassicHttpRequest(Method.GET, url);
            }
            else if(request.getMethod() == RequestMethod.POST){
                client = new BasicClassicHttpRequest(Method.POST, url);;
            }
            else if(request.getMethod() == RequestMethod.PUT){
                client = new BasicClassicHttpRequest(Method.PUT, url);;
            }
            else if(request.getMethod() == RequestMethod.DELETE){
                client = new BasicClassicHttpRequest(Method.DELETE, url);;
            }
            else{
                client = new BasicClassicHttpRequest(Method.POST, url);;
            }
        }else{
            client = new BasicClassicHttpRequest(Method.POST, url);;
        }

        client.setVersion(this.httpVersion);

        Map<String , String> headers = request.getHeaders();
        Map<String , String> lowercaseHeaders = request.getHeadersWithLowercaseKey();
        if(lowercaseHeaders.get("cache-control") == null){
            request.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            client.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        }
        for(Map.Entry<String, String> e:headers.entrySet()){
            client.addHeader(e.getKey(), e.getValue());
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
            client.setEntity(entity);
        }

        return client;

    }

    private void setGetParam() throws Exception{
        String url = request.getUrl().toString();
        String p = getParams();

        if(!"".equals(p)){
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

        if(!"".equals(p)){
            request.getUpStream().addFirst(p);
        }
    }

    private String getParams() throws UnsupportedEncodingException {
        Map<String, List<String>> params = request.getParam().getParam();

        StringBuilder builder = new StringBuilder();
        int n = 0;
        for(Map.Entry<String, List<String>> e: params.entrySet()){
            if(n != 0){
                builder.append("&");
            }
            for(int i=0;i<e.getValue().size();i++){
                builder.append(e.getKey()).append("=");
                String value = e.getValue().get(i);
                if(value == null) {
                    value = "";
                }
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
