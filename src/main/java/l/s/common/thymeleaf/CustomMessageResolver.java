package l.s.common.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;

public abstract class CustomMessageResolver extends AbstractMessageResolver {
    @Override
    public final String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return new StandardMessageResolver().createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }
}
