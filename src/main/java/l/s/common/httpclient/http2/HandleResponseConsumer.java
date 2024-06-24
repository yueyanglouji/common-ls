package l.s.common.httpclient.http2;

import l.s.common.httpclient.common.Response;
import l.s.common.httpclient.common.ResponseContent;
import l.s.common.httpclient.common.ResponseHeader;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HandleResponseConsumer<T> implements AsyncResponseConsumer<T> {

    private final Supplier<ResponseAsyncEntityConsumer<T>> dataConsumerSupplier;
    private final AtomicReference<ResponseAsyncEntityConsumer<T>> dataConsumerRef;

    final Response result = new Response();

    public HandleResponseConsumer(final Supplier<ResponseAsyncEntityConsumer<T>> dataConsumerSupplier) {
        this.dataConsumerSupplier = Args.notNull(dataConsumerSupplier, "Data consumer supplier");
        this.dataConsumerRef = new AtomicReference<>();

        ResponseHeader responseHeader = new ResponseHeader();
        result.setHeader(responseHeader);
        ResponseContent responseContent = new ResponseContent();
        result.setContent(responseContent);
    }

    public HandleResponseConsumer(final ResponseAsyncEntityConsumer<T> dataConsumer) {
        this(() -> dataConsumer);
    }

    @Override
    public void consumeResponse(
            final HttpResponse response,
            final EntityDetails entityDetails,
            final HttpContext httpContext, final FutureCallback<T> resultCallback) throws HttpException, IOException {
        Args.notNull(response, "Response");


        Header[] headers = response.getHeaders();
        if(headers != null){
            for (Header header : headers) {
                result.getHeader().addHeader(header.getName(), header.getValue());
            }
        }
        result.getHeader().setHttpVersion(response.getVersion().toString());
        result.getHeader().setStatusCode(String.valueOf(response.getCode()));
        result.getHeader().setStatusDescription(response.getReasonPhrase());

        if (entityDetails != null) {
            final ResponseAsyncEntityConsumer<T> dataConsumer = dataConsumerSupplier.get();
            if (dataConsumer == null) {
                throw new HttpException("Supplied data consumer is null");
            }
            dataConsumer.setResponse(result);
            dataConsumerRef.set(dataConsumer);
            dataConsumer.streamStart(entityDetails, new FutureCallback<T>() {
                @Override
                public void completed(final T body) {
                    if (resultCallback != null) {
                        resultCallback.completed(body);
                    }
                }
                @Override
                public void failed(final Exception ex) {
                    if (resultCallback != null) {
                        resultCallback.failed(ex);
                    }
                }

                @Override
                public void cancelled() {
                    if (resultCallback != null) {
                        resultCallback.cancelled();
                    }
                }
            });
        } else {
            if (resultCallback != null) {
                resultCallback.completed(null);
            }
        }
    }

    @Override
    public void informationResponse(final HttpResponse response, final HttpContext httpContext) throws HttpException, IOException {
    }

    @Override
    public void updateCapacity(final CapacityChannel capacityChannel) throws IOException {
        final AsyncEntityConsumer<T> dataConsumer = dataConsumerRef.get();
        dataConsumer.updateCapacity(capacityChannel);
    }

    @Override
    public void consume(final ByteBuffer src) throws IOException {
        final AsyncEntityConsumer<T> dataConsumer = dataConsumerRef.get();
        dataConsumer.consume(src);
    }

    @Override
    public void streamEnd(final List<? extends Header> trailers) throws HttpException, IOException {
        final AsyncEntityConsumer<T> dataConsumer = dataConsumerRef.get();
        dataConsumer.streamEnd(trailers);
    }

    @Override
    public void failed(final Exception cause) {
        result.setException(cause);
        result.setSuccess(false);
        final AsyncEntityConsumer<T> dataConsumer = dataConsumerRef.get();
        if (dataConsumer != null) {
            dataConsumer.failed(cause);
        }
        releaseResources();
    }

    @Override
    public void releaseResources() {
        final AsyncEntityConsumer<T> dataConsumer = dataConsumerRef.getAndSet(null);
        if (dataConsumer != null) {
            dataConsumer.releaseResources();
        }
    }

}