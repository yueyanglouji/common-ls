package l.s.common.httpclient;

import l.s.common.httpclient.http1.Http1ClientBuilder;
import l.s.common.httpclient.http2.Http2ClientBuilder;

public class HttpClientBuilder {
    public static AbstractHttpClientBuilder create(HttpClientType type){
        if(type == HttpClientType.HTTP_2_CLIENT){
            return Http2ClientBuilder.create();
        }else {
            return Http1ClientBuilder.create();
        }
    }
}
