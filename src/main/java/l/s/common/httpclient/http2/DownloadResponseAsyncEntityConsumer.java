package l.s.common.httpclient.http2;

import l.s.common.httpclient.HttpClientType;
import l.s.common.httpclient.common.DownloadDevice;
import l.s.common.httpclient.common.Response;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.nio.entity.AbstractBinDataConsumer;
import org.apache.hc.core5.util.Args;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class DownloadResponseAsyncEntityConsumer<T> extends AbstractBinDataConsumer implements ResponseAsyncEntityConsumer<T> {

    private final OutputStream download;

    private final WritableByteChannel channel;

    private final DownloadDevice downloadDevice;

    private volatile long contentLength;

    private long length;

    private volatile FutureCallback<T> resultCallback;

    private volatile Response response;

    public DownloadResponseAsyncEntityConsumer(OutputStream out){
        this.download = out;
        channel = Channels.newChannel(out);
        downloadDevice = new DownloadDevice(HttpClientType.HTTP_2_CLIENT);
    }

    @Override
    public void releaseResources() {
        try {
            download.flush();
            download.close();
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void streamStart(EntityDetails entityDetails, FutureCallback<T> resultCallback) throws HttpException, IOException {
        Args.notNull(resultCallback, "Result callback");
        this.resultCallback = resultCallback;
        contentLength = entityDetails.getContentLength();
        downloadDevice.setResponse(response);
    }

    @Override
    protected final void completed() throws IOException {
        response.setSuccess(true);
        downloadDevice.setStatus(true);
        downloadDevice.setRate(1);
        downloadDevice.setSize(length / 1024.0F / 1024.0F);
        downloadDevice.setWaite(false);
        if (resultCallback != null) {
            resultCallback.completed((T) downloadDevice);
        }
        releaseResources();
    }

    @Override
    public final void failed(final Exception cause) {
        if (resultCallback != null) {
            resultCallback.failed(cause);
        }
        releaseResources();
    }

    @Override
    public T getContent() {
        return (T) downloadDevice;
    }
    @Override
    protected int capacityIncrement() {
        return 10240;
    }

    @Override
    protected void data(ByteBuffer src, boolean endOfStream) throws IOException {
        if (src == null) {
            return;
        }
        int remaining = src.remaining();
        length += remaining;
        channel.write(src);
        if(contentLength < 0){
            downloadDevice.setRate(-1);
        }else {
            downloadDevice.setRate((float)(length * 1.0/contentLength));
        }
        downloadDevice.setSize(length / 1024.0F / 1024.0F);
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
    }

    public DownloadDevice getDownloadDevice() {
        return downloadDevice;
    }
}
