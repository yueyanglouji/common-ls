package l.s.common.httpclient.common;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.SAFE)
public class CookieStore extends BasicCookieStore{

	private static final long serialVersionUID = 599191980684059315L;

	
}
