package l.s.common.thymeleaf;

import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import java.util.Locale;

public class ThymeleafContext extends AbstractThymeleafContext{

    @Override
    public IContext getContext(){
        AbstractContext context = new Context();
        Locale locale = this.getLocale();
        if(locale == null){
            locale = this.locale;
        }
        context.setLocale(locale);
        context.setVariables(variables);

        return context;
    }
}
