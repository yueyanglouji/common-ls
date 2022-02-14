package l.s.common.thymeleaf;

import l.s.common.context.ThreadLocalContext;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ThymeleafContext {

    private final Map<String,Object> variables;

    private Locale locale;

    public ThymeleafContext(){
        this.locale = Locale.getDefault();
        this.variables = new LinkedHashMap<>();
    }

    public IContext getContext(){
        Object httpServletRequest = ThreadLocalContext.getContext().getAttribute("_ls_thymeleaf_request");
        Object httpServletResponse = ThreadLocalContext.getContext().getAttribute("_ls_thymeleaf_response");

        AbstractContext context;
        if(httpServletRequest!=null && httpServletResponse!=null){
            HttpServletRequest request = (HttpServletRequest)httpServletRequest;
            HttpServletResponse response = (HttpServletResponse)httpServletResponse;
            IWebExchange webExchange = JavaxServletWebApplication.buildApplication(request.getServletContext()).buildExchange(request, response);
            context = new WebContext(webExchange);
        }else{
            context = new Context();
        }

        Locale locale = ThreadLocalContext.getContext().getAttribute("_ls_thymeleaf_thread_locale", Locale.class);
        if(locale == null){
            locale = this.locale;
        }
        context.setLocale(locale);
        context.setVariables(variables);

        return context;
    }

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

    public void setHttpServletRequestAndResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ThreadLocalContext.getContext().setAttribute("_ls_thymeleaf_request", httpServletRequest);
        ThreadLocalContext.getContext().setAttribute("_ls_thymeleaf_response", httpServletResponse);
    }

    public void setVariable(String key, Object value){
        this.variables.put(key, value);
    }
}
