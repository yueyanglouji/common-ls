package l.s.common.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GlobalContext extends Context implements Serializable{

	private static GlobalContext context;

	private GlobalContext(){
		super();
	}
	
	public static GlobalContext getContext() {
		if(context == null){
			context = new GlobalContext();
		}
        return context;
    }
}
