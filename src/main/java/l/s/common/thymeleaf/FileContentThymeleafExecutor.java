package l.s.common.thymeleaf;

import org.thymeleaf.TemplateEngine;

import java.util.Locale;

public class FileContentThymeleafExecutor extends ThymeleafExecutor<FileContentThymeleafExecutor>{

    FileContentThymeleaf fileContentThymeleaf;

    FileContentThymeleafExecutor(Thymeleaf.CONTEXT_TYPE contextType, TemplateEngine engine, Locale locale, FileContentThymeleaf fileContentThymeleaf){
        super(contextType, engine, locale);
        this.fileContentThymeleaf = fileContentThymeleaf;
    }

    @Override
    public String process(String template){
        try{
            return super.process(fileContentThymeleaf.getFileContent(template));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
