package thymeleaf

import l.s.common.thymeleaf.FileContentThymeleaf
import l.s.common.thymeleaf.FilePathThymeleaf
import l.s.common.thymeleaf.ThymeleafFactory
import org.jsoup.Jsoup
import org.thymeleaf.templatemode.TemplateMode

class FileContentTymeleafTest {
    public static void main(String[] args) {
        FileContentThymeleaf thymeleaf = ThymeleafFactory.getFileContentThymeleaf(TemplateMode.HTML, "UTF-8", "D:\\lixiaobao\\git\\common-ls\\src\\test\\resources\\", ".html", true);
        thymeleaf.setVariable("test", [abc:"abc", def:"def"])

        //println thymeleaf.process("mailTemplate.html");

        String content = thymeleaf.getFileContent("mailTemplate")
        println content
        println content.matches("[\\s\\S]*xmlns\\:th\\s*=\\s*\"http\\://www\\.thymeleaf\\.org.*\"[\\s\\S]*")
        //println thymeleaf.process("mailTemplate");

    }
}
