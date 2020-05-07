package thymeleaf

import l.s.common.thymeleaf.CustomMessageResolver
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.messageresolver.AbstractMessageResolver
import org.thymeleaf.messageresolver.IMessageResolver

class CustomResolver extends CustomMessageResolver{

    def map = [:];

    @Override
    String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        String ret = map.get(key);
        if(ret == null){
            if(key.matches(/db\..*/))
            return ""
        }
        return ret;
    }

}
