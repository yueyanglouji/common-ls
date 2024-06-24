package l.s.common.httpclient;

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

public interface Async {

    <T> Future<T> execute(
            final ClassicHttpRequest request,
            final HttpClientContext localContext,
            final HttpClientResponseHandler<T> handler, final FutureCallback<T> callback);

    <T> Future<T> execute(final ClassicHttpRequest request,
                                 final HttpClientContext localContext,
                                 final HttpClientResponseHandler<T> handler);

    <T> Future<T> execute(AsyncRequestProducer requestProducer,
                                 AsyncResponseConsumer<T> responseConsumer,
                                 HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
                                 HttpContext context,
                                 final FutureCallback<T> callback);

    <T> Future<T> execute(AsyncRequestProducer requestProducer,
                                 AsyncResponseConsumer<T> responseConsumer,
                                 HttpClientContext context);

}
