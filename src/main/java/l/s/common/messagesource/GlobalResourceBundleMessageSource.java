package l.s.common.messagesource;

import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.List;

public class GlobalResourceBundleMessageSource extends ResourceBundleMessageSource {

    public static GlobalResourceBundleMessageSource messageSource;

    private GlobalResourceBundleMessageSource(){
        setDefaultEncoding("UTF-8");
    }

    public static GlobalResourceBundleMessageSource getInstance(){
        if(messageSource == null){
            messageSource = new GlobalResourceBundleMessageSource();
        }
        return messageSource;
    }

    public void setBasenames(List<String> basenames){
        super.getBasenameSet().clear();
        this.addBasenames(basenames);
    }

    public void addBasename(String basename){
        super.addBasenames(basename);
    }

    public void addBasenames(List<String> basenames){
        for(String basename : basenames){
            super.addBasenames(basename);
        }
    }

}
