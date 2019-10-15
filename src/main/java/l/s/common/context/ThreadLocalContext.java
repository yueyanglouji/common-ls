package l.s.common.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ThreadLocalContext extends Context implements Serializable{

	private static ThreadLocal<ThreadLocalContext> context = new ThreadLocal<ThreadLocalContext>();

	private ThreadLocalContext(){
	}
	
	public static ThreadLocalContext getContext() {
		if(context == null || context.get() == null){
			context.set(new ThreadLocalContext());
		}
        return context.get();
    }
}
