package l.s.common.thymeleaf;

import l.s.common.messagesource.GlobalResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;

public class TemplateMessageResolver extends AbstractMessageResolver {

    private final StandardMessageResolver standardMessageResolver;

    private MessageSource messageSource;

    public TemplateMessageResolver() {
        super();
        this.standardMessageResolver = new StandardMessageResolver();
    }

    public final MessageSource getMessageSource() {
        return this.messageSource;
    }

    public final StandardMessageResolver getStandardMessageResolver() {
        return this.standardMessageResolver;
    }

    public final void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        /*
         * FIRST STEP: Look for the message using template-based resolution
         */
        if (context != null) {
            try {
                if(messageSource == null){
                    this.messageSource = GlobalResourceBundleMessageSource.getInstance();
                }
                return this.messageSource.getMessage(key, messageParameters, context.getLocale());
            } catch (NoSuchMessageException e) {
                // Try other methods
            }

        }

        final String message =
                this.standardMessageResolver.resolveMessage(context, origin, key, messageParameters, false, true, true);
        if (message != null) {
            return message;
        }



        /*
         * NOT FOUND, return null
         */
        return null;

    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return this.standardMessageResolver.createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }
}
