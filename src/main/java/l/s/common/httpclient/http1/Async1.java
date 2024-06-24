package l.s.common.httpclient.http1;

import l.s.common.httpclient.Async;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.concurrent.Future;


public class Async1 implements Async {

    private CloseableHttpClient executor;
    private java.util.concurrent.Executor concurrentExec;

    public static Async1 newInstance() {
        return new Async1();
    }

    Async1() {
        super();
    }

    public Async1 use(final CloseableHttpClient executor) {
        this.executor = executor;
        return this;
    }

    public Async1 use(final java.util.concurrent.Executor concurrentExec) {
        this.concurrentExec = concurrentExec;
        return this;
    }

    static class ExecRunnable<T> implements Runnable {

        private final BasicFuture<T> future;
        private final ClassicHttpRequest request;

        private final HttpClientContext localContext;
        private final CloseableHttpClient executor;
        private final HttpClientResponseHandler<T> handler;

        ExecRunnable(
                final BasicFuture<T> future,
                final ClassicHttpRequest request,
                final HttpClientContext localContext,
                final CloseableHttpClient executor,
                final HttpClientResponseHandler<T> handler) {
            super();
            this.future = future;
            this.request = request;
            this.localContext = localContext;
            this.executor = executor;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                final T result = this.executor.execute(null, this.request, localContext, handler);
                this.future.completed(result);
            } catch (final Exception ex) {
                this.future.failed(ex);
            }
        }
    }

    @Override
    public <T> Future<T> execute(
            final ClassicHttpRequest request,
            final HttpClientContext localContext,
            final HttpClientResponseHandler<T> handler, final FutureCallback<T> callback) {
        final BasicFuture<T> future = new BasicFuture<>(callback);
        final ExecRunnable<T> runnable = new ExecRunnable<>(
                future,
                request,
                localContext,
                this.executor,
                handler);
        if (this.concurrentExec != null) {
            this.concurrentExec.execute(runnable);
        } else {
            final Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.start();
        }
        return future;
    }

    @Override
    public <T> Future<T> execute(final ClassicHttpRequest request,
                                 final HttpClientContext localContext,
                                 final HttpClientResponseHandler<T> handler) {
        return execute(request, localContext, handler, null);
    }

    @Override
    public <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context, FutureCallback<T> callback) {
        throw new RuntimeException("Not support in http1 Async.");
    }

    @Override
    public <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HttpClientContext context) {
        throw new RuntimeException("Not support in http1 Async.");
    }

}
