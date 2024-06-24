package l.s.common.httpclient.http2;

import l.s.common.httpclient.common.Response;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;

public interface ResponseAsyncEntityConsumer<T> extends AsyncEntityConsumer<T> {

    void setResponse(Response response);

}
