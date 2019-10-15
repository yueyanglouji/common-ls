package l.s.common.httpclient;

import java.util.concurrent.TimeUnit;

public class Timeout{
	
	private org.apache.hc.core5.util.Timeout t;
	
    public static Timeout ofDays(final long days) {
        return of(days, TimeUnit.DAYS);
    }

    public static Timeout ofHours(final long hours) {
        return of(hours, TimeUnit.HOURS);
    }

    public static Timeout ofMicroseconds(final long microseconds) {
        return of(microseconds, TimeUnit.MICROSECONDS);
    }

    public static Timeout ofMillis(final long milliseconds) {
        return of(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static Timeout ofMinutes(final long minutes) {
        return of(minutes, TimeUnit.MINUTES);
    }

    public static Timeout ofNanoseconds(final long nanoseconds) {
        return of(nanoseconds, TimeUnit.NANOSECONDS);
    }

    public static Timeout ofSeconds(final long seconds) {
        return of(seconds, TimeUnit.SECONDS);
    }
    
    public static Timeout ofTimeout(org.apache.hc.core5.util.Timeout t) {
    	Timeout r = new Timeout();
    	r.t = t;
        return r;
    }
    
    public static Timeout of(final long duration, final TimeUnit timeUnit) {
    	Timeout t = new Timeout();
        t.t = org.apache.hc.core5.util.Timeout.of(duration, timeUnit);
        return t;
    }
	
    public org.apache.hc.core5.util.Timeout get(){
    	return t;
    }
}
