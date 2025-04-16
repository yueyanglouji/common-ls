package l.s.common.thymeleaf;

import org.thymeleaf.TemplateEngine;

import java.util.Locale;

public abstract class ThymeleafExecutor<T> {

    protected AbstractThymeleafContext context;

    protected TemplateEngine engine;

    ThymeleafExecutor(Thymeleaf.CONTEXT_TYPE contextType, TemplateEngine engine, Locale locale){
        this.engine = engine;
        if(contextType == Thymeleaf.CONTEXT_TYPE.jakartaServlet){
            this.context = new JakartaThymeleafContext();
        }else if(contextType == Thymeleaf.CONTEXT_TYPE.javaxServlet) {
            this.context = new JavaxThymeleafContext();
        }else {
            this.context = new ThymeleafContext();
        }
        this.context.setLocale(locale);
    }

    public T setLocal(Locale locale){
        context.setLocale(locale);
        return (T)this;
    }

    public T setVariable(String name, Object value){
        context.setVariable(name, value);
        return (T)this;
    }

    /**
     *
     * @param request javax.http.HttpServletRequest or jakarta.http.HttpServletRequest
     * @param response javax.http.HttpServletResponse or jakarta.http.HttpServletResponse
     * @return this
     */
    public T setWebContext(Object request, Object response){
        context.setHttpServletRequestAndResponse(request, response);
        return (T)this;
    }

    public String process(String template){
        return engine.process(template, context.getContext());
    }

}
