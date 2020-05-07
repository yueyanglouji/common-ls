package thymeleaf

import l.s.common.messagesource.GlobalResourceBundleMessageSource
import l.s.common.thymeleaf.FilePathThymeleaf
import l.s.common.thymeleaf.ThymeleafFactory
import org.thymeleaf.templatemode.TemplateMode

class FilePathTymeleafTest {
    public static void main(String[] args) {
        FilePathThymeleaf thymeleaf = ThymeleafFactory.getFilePathThymeleaf(TemplateMode.HTML, "UTF-8", "D:\\lixiaobao\\git\\common-ls\\src\\test\\resources\\", ".html", true);
        GlobalResourceBundleMessageSource gbms = GlobalResourceBundleMessageSource.getInstance();
        gbms.setBasename("messagesource/ms")
        thymeleaf.setVariable("test", [abc:"abc", def:"def"])

        //println thymeleaf.process("mailTemplate.html");

        thymeleaf.addDefaultMessage("test.abctest1", "sdsfsfdsf")
        thymeleaf.addExtentMessageResolver(new CustomResolver())

        println thymeleaf.process("mailTemplate");

    }
}
