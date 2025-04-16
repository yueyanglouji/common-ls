package l.s.common.context;

import java.io.Serializable;

public class ThreadLocalContext extends Context implements Serializable{

	private static final ThreadLocal<ThreadLocalContext> context = new ThreadLocal<>();

	private ThreadLocalContext(){
		super();
	}
	
	public static ThreadLocalContext getContext() {
		if(context.get() == null){
			context.set(new ThreadLocalContext());
		}
        return context.get();
    }

	public static void remove() {
		context.remove();
	}
}
