package l.s.common.thymeleaf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.Locale;

public class JakartaThymeleafContext extends AbstractThymeleafContext{

    @Override
    public IContext getContext(){
        Object httpServletRequest = this.httpServletRequest;
        Object httpServletResponse = this.httpServletResponse;

        AbstractContext context;
        if(httpServletRequest!=null && httpServletResponse!=null){
            HttpServletRequest request = (HttpServletRequest)httpServletRequest;
            HttpServletResponse response = (HttpServletResponse)httpServletResponse;
            IWebExchange webExchange = JakartaServletWebApplication.buildApplication(request.getServletContext()).buildExchange(request, response);
            context = new WebContext(webExchange);
        }else{
            context = new Context();
        }

        Locale locale = this.getLocale();
        if(locale == null){
            locale = this.locale;
        }
        context.setLocale(locale);
        context.setVariables(variables);

        return context;
    }
}
