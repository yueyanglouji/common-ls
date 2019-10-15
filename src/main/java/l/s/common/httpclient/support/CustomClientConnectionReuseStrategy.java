package l.s.common.httpclient.support;

import java.util.Iterator;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.message.BasicTokenIterator;
import org.apache.hc.core5.http.protocol.HttpContext;

public class CustomClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy{

	//public static final DefaultConnectionReuseStrategy INSTANCE = new CustomClientConnectionReuseStrategy();

	@Override
    public boolean keepAlive(final HttpRequest request, final HttpResponse response, final HttpContext context) {

        try {
			if (request != null) {
				final Header[] connHeaders = request.getHeaders(HttpHeaders.CONNECTION);
				if (connHeaders.length != 0) {
					final Iterator<String> ti = new BasicTokenIterator(request.headerIterator(HttpHeaders.CONNECTION));
					while (ti.hasNext()) {
						final String token = ti.next();
						if (HeaderElements.CLOSE.equalsIgnoreCase(token)) {
							return false;
						}
					}
				}
	        }
		} catch (Exception e) {
		}
        return super.keepAlive(request, response, context);
    }

}
