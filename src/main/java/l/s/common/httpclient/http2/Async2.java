package l.s.common.httpclient.http2;

import l.s.common.httpclient.Async;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.concurrent.Future;


public class Async2 implements Async {

    private CloseableHttpAsyncClient executor;

    public static Async2 newInstance() {
        return new Async2();
    }

    Async2() {
        super();
    }

    public Async2 use(final CloseableHttpAsyncClient executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public <T> Future<T> execute(ClassicHttpRequest request, HttpClientContext localContext, HttpClientResponseHandler<T> handler, FutureCallback<T> callback) {
        throw new RuntimeException("Not support in http2 Async.");
    }

    @Override
    public <T> Future<T> execute(ClassicHttpRequest request, HttpClientContext localContext, HttpClientResponseHandler<T> handler) {
        throw new RuntimeException("Not support in http2 Async.");
    }

    @Override
    public <T> Future<T> execute(AsyncRequestProducer requestProducer,
                                 AsyncResponseConsumer<T> responseConsumer,
                                 HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
                                 HttpContext context,
                                 final FutureCallback<T> callback) {

        return executor.execute(requestProducer, responseConsumer, pushHandlerFactory, context, callback);
    }

    @Override
    public <T> Future<T> execute(AsyncRequestProducer requestProducer,
                                 AsyncResponseConsumer<T> responseConsumer,
                                 HttpClientContext context) {
        return execute(requestProducer, responseConsumer, null, context, null);
    }

}
