package l.s.common.httpclient.http2;

import l.s.common.httpclient.common.Response;
import l.s.common.httpclient.common.ResponseContent;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.nio.entity.AbstractCharDataConsumer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.CharArrayBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public class ContentResponseAsyncEntityConsumer<T> extends AbstractCharDataConsumer implements ResponseAsyncEntityConsumer<T> {

    private final int capacityIncrement;
    private final CharArrayBuffer content;

    private volatile FutureCallback<T> resultCallback;

    private final String defaultCharset;

    private final String charset;

    private volatile Response response;

    public ContentResponseAsyncEntityConsumer(final int bufSize, final int capacityIncrement,
                                              String charset, String defaultCharset) {
        super(bufSize, CharCodingConfig.custom().setCharset(StandardCharsets.UTF_8).build());
        this.capacityIncrement = Args.positive(capacityIncrement, "Capacity increment");
        this.content = new CharArrayBuffer(1024);
        this.charset = charset;
        this.defaultCharset = defaultCharset;
    }

    public ContentResponseAsyncEntityConsumer(final int capacityIncrement) {
        this(DEF_BUF_SIZE, capacityIncrement, null, "UTF-8");
    }

    public ContentResponseAsyncEntityConsumer(String charset, String defaultCharset) {
        this(DEF_BUF_SIZE, Integer.MAX_VALUE, charset, defaultCharset);
    }

    public ContentResponseAsyncEntityConsumer() {
        this(Integer.MAX_VALUE);
    }

    @Override
    public final void streamStart(
            final EntityDetails entityDetails,
            final FutureCallback<T> resultCallback) throws IOException, HttpException {
        Args.notNull(resultCallback, "Result callback");
        this.resultCallback = resultCallback;
        try {
            final ContentType contentType = entityDetails != null ? ContentType.parse(entityDetails.getContentType()) : null;
            if(charset == null){
                setCharset(ContentType.getCharset(contentType, Charset.forName(defaultCharset)));
            }else {
                setCharset(Charset.forName(charset));
            }
        } catch (final UnsupportedCharsetException ex) {
            throw new UnsupportedEncodingException(ex.getMessage());
        }
    }

    @Override
    protected final void completed() throws IOException {
        T result = getContent();
        if (resultCallback != null) {
            resultCallback.completed(result);
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
        ResponseContent responseContent = new ResponseContent();
        responseContent.setContent(content.toString());
        response.setContent(responseContent);
        response.setSuccess(true);
        return (T) response;
    }

    @Override
    protected int capacityIncrement() {
        final int available = content.capacity() - content.length();
        return Math.max(capacityIncrement, available);
    }

    @Override
    protected void data(CharBuffer src, boolean endOfStream) throws IOException {
        Args.notNull(src, "CharBuffer");
        final int chunk = src.remaining();
        content.ensureCapacity(chunk);
        src.get(content.array(), content.length(), chunk);
        content.setLength(content.length() + chunk);
    }

    @Override
    public void releaseResources() {
        content.clear();
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
    }
}
