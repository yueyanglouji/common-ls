package l.s.common.thymeleaf;

import org.thymeleaf.TemplateEngine;

import java.util.Locale;

public class FilePathThymeleafExecutor extends ThymeleafExecutor<FilePathThymeleafExecutor>{

    FilePathThymeleafExecutor(Thymeleaf.CONTEXT_TYPE contextType, TemplateEngine engine, Locale locale){
        super(contextType, engine, locale);
    }

}
