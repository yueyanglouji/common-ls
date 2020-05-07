package l.s.common.messagesource;

import l.s.common.context.Application;
import l.s.common.context.Context;
import l.s.common.context.GlobalContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

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

    protected Locale getDefaultLocale() {
        return GlobalContext.getContext().getLocale();
    }

}
