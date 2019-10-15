package l.s.common.messagesource;

import l.s.common.context.Application;
import l.s.common.context.Context;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageSourceHandler extends org.springframework.context.support.MessageSourceAccessor {

    private MessageSourceHandler(Context context){
        this(GlobalResourceBundleMessageSource.getInstance(), context.getLocale());
    }

    private MessageSourceHandler(MessageSource messageSource, Locale defaultLocale) {
        super(messageSource, defaultLocale);
    }

    public static MessageSourceHandler getInstance(Context context){
        return new MessageSourceHandler(context);
    }

    public static MessageSourceHandler getInstance(){
        return new MessageSourceHandler(Application.getContext());
    }

}
