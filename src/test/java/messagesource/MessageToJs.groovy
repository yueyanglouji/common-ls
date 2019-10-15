package messagesource

import l.s.common.context.Application
import l.s.common.context.GlobalContext
import l.s.common.messagesource.GlobalResourceBundleMessageSource
import l.s.common.messagesource.MessageSourceHandler

class MessageToJs {

    public static void main(String[] args) {
        GlobalResourceBundleMessageSource source = GlobalResourceBundleMessageSource.getInstance();
        source.addBasename("messagesource/ms")
        source.addBasename("messagesource/message")

        GlobalContext.getContext().setLocale(Locale.JAPAN)
        Application.setContext(GlobalContext.getContext())

        String message = MessageSourceHandler.getInstance().getMessage("ms.message.xml.FileName")
        println message


    }

}
