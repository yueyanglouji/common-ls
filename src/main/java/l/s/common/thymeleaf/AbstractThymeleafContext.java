package l.s.common.thymeleaf;

import l.s.common.context.ThreadLocalContext;
import org.thymeleaf.context.IContext;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractThymeleafContext {

    protected final Map<String,Object> variables;

    protected Locale locale;

    protected Object httpServletRequest;

    protected Object httpServletResponse;

    public AbstractThymeleafContext(){
        this.locale = Locale.getDefault();
        this.variables = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public abstract IContext getContext();

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setHttpServletRequestAndResponse(Object httpServletRequest, Object httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public void setVariable(String key, Object value){
        this.variables.put(key, value);
    }
}
