package l.s.common.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class Context {

    protected Map<String, Object> map;

    protected Locale locale;

    protected Context(){
        map = new HashMap<>();
        locale = Locale.getDefault();
    }

    public void setAttribute(String key, Object value){
        map.put(key, value);
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

    public Locale getLocale(){
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
