package l.s.common.thymeleaf;

import l.s.common.context.ThreadLocalContext;
import org.thymeleaf.context.IContext;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractThymeleafContext {

    protected final Map<String,Object> variables;

    protected Locale locale;

    public AbstractThymeleafContext(){
        this.locale = Locale.getDefault();
        this.variables = new LinkedHashMap<>();
    }

    public abstract IContext getContext();

    public Locale getGlobalLocale() {
        return locale;
    }

    public void setGlobalLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getThreadLocale() {
        return ThreadLocalContext.getContext().getAttribute("_ls_thymeleaf_thread_locale", Locale.class);
    }

    public void setThreadLocale(Locale locale) {
        ThreadLocalContext.getContext().setAttribute("_ls_thymeleaf_thread_locale", locale);
    }

    public void setHttpServletRequestAndResponse(Object httpServletRequest, Object httpServletResponse) {
        ThreadLocalContext.getContext().setAttribute("_ls_thymeleaf_request", httpServletRequest);
        ThreadLocalContext.getContext().setAttribute("_ls_thymeleaf_response", httpServletResponse);
    }

    public void setVariable(String key, Object value){
        this.variables.put(key, value);
    }
}
