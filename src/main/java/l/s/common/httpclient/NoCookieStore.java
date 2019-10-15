package l.s.common.httpclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hc.client5.http.cookie.Cookie;

public class NoCookieStore implements org.apache.hc.client5.http.cookie.CookieStore, Serializable{

	private static final long serialVersionUID = -3819447365801840737L;

	@Override
	public void addCookie(Cookie cookie) {
		
	}

	@Override
	public List<Cookie> getCookies() {
		return new ArrayList<>();
	}

	@Override
	public boolean clearExpired(Date date) {
		return false;
	}

	@Override
	public void clear() {
		
	}

}
