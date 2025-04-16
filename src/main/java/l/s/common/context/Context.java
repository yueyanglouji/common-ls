package l.s.common.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Context {

    protected Map<String, Object> map;

    protected Locale locale;

    protected Map<String, Context> contextMap;

    private String defaultContextID = "__default_context__UUID";

    protected Context(){
        this(null);
    }

    protected Context(Map<String, Context> contextMap){
        map = new HashMap<>();
        if(contextMap == null || contextMap.size() == 0){
            contextMap = new HashMap<>();
            contextMap.put(defaultContextID, this);
        }
        this.contextMap = contextMap;
        locale = Locale.getDefault();
    }

    public Context getDefaultContext(){
        return contextMap.get(defaultContextID);
    }

    public Context getContext(String contextId){
        return contextMap.get(contextId);
    }

    public Context createContext(String contextId){
        this.contextMap.put(contextId, new Context(this.contextMap));
        return contextMap.get(contextId);
    }

    public Context setAttribute(String key, Object value){
        map.put(key, value);
        return this;
    }

    public Object getAttribute(String key){
        return  map.get(key);
    }

    @SuppressWarnings("unchecked")
    public<T> T getAttribute(String key, Class<T> clazz){
        Object value = map.get(key);
        return (T)value;
    }

    @SuppressWarnings("unchecked")
    public<T> T getAttribute(String key, T defaultvalue){
        Object value = map.get(key);
        if(value == null){
            return defaultvalue;
        }
        try{
            return (T)value;
        }catch (Throwable e){
            return defaultvalue;
        }
    }

    public void removeAttribute(String key){
        map.remove(key);
    }

    public void reset(){
        map = new HashMap<>();
    }

    public Locale getLocale(){
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
