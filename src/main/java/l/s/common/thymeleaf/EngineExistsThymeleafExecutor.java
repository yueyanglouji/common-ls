package l.s.common.thymeleaf;

import org.thymeleaf.TemplateEngine;

import java.util.Locale;

public class EngineExistsThymeleafExecutor extends ThymeleafExecutor<EngineExistsThymeleafExecutor>{

    EngineExistsThymeleafExecutor(Thymeleaf.CONTEXT_TYPE contextType, TemplateEngine engine, Locale locale){
        super(contextType, engine, locale);
    }

}
