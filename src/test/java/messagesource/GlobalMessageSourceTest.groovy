package messagesource

import l.s.common.messagesource.GlobalResourceBundleMessageSource

class GlobalMessageSourceTest {
    public static void main(String[] args) {
        GlobalResourceBundleMessageSource source = GlobalResourceBundleMessageSource.getInstance();
        source.setBasename("messagesource/ms")


        String message = source.getMessage("ms.message.xml.FileName", new Object[0],  Locale.JAPAN)
        println message
    }
}
