package l.s.common.httpclient.support;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;

@Contract(threading = ThreadingBehavior.STATELESS)
public class CustomHttpRequestRetryStrategy implements HttpRequestRetryStrategy {

    public static final CustomHttpRequestRetryStrategy INSTANCE = new CustomHttpRequestRetryStrategy();

    /**
     * Maximum number of allowed retries
     */
    private final int maxRetries;

    /**
     * Retry interval between subsequent retries
     */
    private final TimeValue defaultRetryInterval;

    /**
     * Derived {@code IOExceptions} which shall not be retried
     */
    private final Set<Class<? extends IOException>> nonRetriableIOExceptionClasses;

    /**
     * HTTP status codes which shall be retried
     */
    private final Set<Integer> retriableCodes;

    protected CustomHttpRequestRetryStrategy(
            final int maxRetries,
            final TimeValue defaultRetryInterval,
            final Collection<Class<? extends IOException>> clazzes,
            final Collection<Integer> codes) {
        Args.notNegative(maxRetries, "maxRetries");
        Args.notNegative(defaultRetryInterval.getDuration(), "defaultRetryInterval");
        this.maxRetries = maxRetries;
        this.defaultRetryInterval = defaultRetryInterval;
        this.nonRetriableIOExceptionClasses = new HashSet<>(clazzes);
        this.retriableCodes = new HashSet<>(codes);
    }

    /**
     * Create the HTTP request retry strategy using the following list of
     * non-retriable I/O exception classes:<br>
     * <ul>
     * <li>InterruptedIOException</li>
     * <li>UnknownHostException</li>
     * <li>ConnectException</li>
     * <li>ConnectionClosedException</li>
     * <li>NoRouteToHostException</li>
     * <li>SSLException</li>
     * </ul>
     *
     * and retriable HTTP status codes:<br>
     * <ul>
     * <li>SC_TOO_MANY_REQUESTS (429)</li>
     * <li>SC_SERVICE_UNAVAILABLE (503)</li>
     * </ul>
     *
     * @param maxRetries how many times to retry; 0 means no retries
     * @param defaultRetryInterval the default retry interval between
     * subsequent retries if the {@code Retry-After} header is not set
     * or invalid.
     */
    public CustomHttpRequestRetryStrategy(
            final int maxRetries,
            final TimeValue defaultRetryInterval) {
        this(maxRetries, defaultRetryInterval,
                Arrays.asList(
                        UnknownHostException.class,
                        ConnectException.class,
                        ConnectionClosedException.class,
                        NoRouteToHostException.class,
                        SSLException.class),
                Arrays.asList(
                        HttpStatus.SC_TOO_MANY_REQUESTS,
                        HttpStatus.SC_SERVICE_UNAVAILABLE));
    }

    /**
     * Create the HTTP request retry strategy with a max retry count of 1,
     * default retry interval of 1 second, and using the following list of
     * non-retriable I/O exception classes:<br>
     * <ul>
     * <li>InterruptedIOException</li>
     * <li>UnknownHostException</li>
     * <li>ConnectException</li>
     * <li>ConnectionClosedException</li>
     * <li>SSLException</li>
     * </ul>
     *
     * and retriable HTTP status codes:<br>
     * <ul>
     * <li>SC_TOO_MANY_REQUESTS (429)</li>
     * <li>SC_SERVICE_UNAVAILABLE (503)</li>
     * </ul>
     */
    public CustomHttpRequestRetryStrategy() {
        this(1, TimeValue.ofSeconds(1L));
    }

    @Override
    public boolean retryRequest(
            final HttpRequest request,
            final IOException exception,
            final int execCount,
            final HttpContext context) {
        Args.notNull(request, "request");
        Args.notNull(exception, "exception");

        if (execCount > this.maxRetries) {
            // Do not retry if over max retries
            return false;
        }
        if (this.nonRetriableIOExceptionClasses.contains(exception.getClass())) {
            return false;
        } else {
            for (final Class<? extends IOException> rejectException : this.nonRetriableIOExceptionClasses) {
                if (rejectException.isInstance(exception)) {
                    return false;
                }
            }
        }
        if (request instanceof CancellableDependency && ((CancellableDependency) request).isCancelled()) {
            return false;
        }

        // Retry if the request is considered idempotent
        return handleAsIdempotent(request);
    }

    @Override
    public boolean retryRequest(
            final HttpResponse response,
            final int execCount,
            final HttpContext context) {
        Args.notNull(response, "response");

        return execCount <= this.maxRetries && retriableCodes.contains(response.getCode());
    }

    @Override
    public TimeValue getRetryInterval(
            final HttpResponse response,
            final int execCount,
            final HttpContext context) {
        Args.notNull(response, "response");

        final Header header = response.getFirstHeader(HttpHeaders.RETRY_AFTER);
        TimeValue retryAfter = null;
        if (header != null) {
            final String value = header.getValue();
            try {
                retryAfter = TimeValue.ofSeconds(Long.parseLong(value));
            } catch (final NumberFormatException ignore) {
                final Instant retryAfterDate = DateUtils.parseStandardDate(value);
                if (retryAfterDate != null) {
                    retryAfter =
                            TimeValue.ofMilliseconds(retryAfterDate.toEpochMilli() - System.currentTimeMillis());
                }
            }

            if (TimeValue.isPositive(retryAfter)) {
                return retryAfter;
            }
        }
        return this.defaultRetryInterval;
    }

    protected boolean handleAsIdempotent(final HttpRequest request) {
        return Method.isIdempotent(request.getMethod());
    }

}
