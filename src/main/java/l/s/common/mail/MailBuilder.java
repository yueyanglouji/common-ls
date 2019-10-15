package l.s.common.mail;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MailBuilder {

    private Mail mail;

    public MailBuilder(MailService service){
        mail = new Mail();
        mail.charset = service.getDefault_charset();
        mail.messageType = service.getDefault_content_type();
        mail.from = service.getDefault_from();
    }

    public Mail build() {
        return mail;
    }

    public MailBuilder to(String... tos) {
        to(Arrays.asList(tos));
        return this;
    }

    public MailBuilder to(List<String> tos) {

        if(tos == null || tos.size() == 0){
            throw new RuntimeException("to set null");
        }
        for(String to : tos){
            mail.to.add(to);
        }
        return this;
    }

    public MailBuilder cc(String... ccs) {
        cc(Arrays.asList(ccs));
        return this;
    }

    public MailBuilder cc(List<String> ccs) {
        if(ccs == null || ccs.size() == 0){
            throw new RuntimeException("cc set null");
        }
        for(String cc : ccs){
            mail.cc.add(cc);
        }
        return this;
    }

    public MailBuilder bcc(String... bccs) {
        bcc(Arrays.asList(bccs));
        return this;
    }

    public MailBuilder bcc(List<String> bccs) {
        if(bccs == null || bccs.size() == 0){
            throw new RuntimeException("bcc set null");
        }
        for(String bcc : bccs){
            mail.bcc.add(bcc);
        }
        return this;
    }

    public MailBuilder from(String from){
        mail.from = from;
        return this;
    }

    public MailBuilder subject(String subject){
        mail.subject = subject;
        return this;
    }

    public MailBuilder attachment_path(String filePath) {
        FileSystemResource resource = new FileSystemResource(filePath);
        String name = resource.getFilename();
        mail.attachments.add(new MailResourcePair(name, resource));
        return this;
    }

    public MailBuilder message_path(String filePath) {
        FileSystemResource resource = new FileSystemResource(filePath);
        mail.messageResource = resource;
        return this;
    }

    public MailBuilder attachment_path(String filePath, String name) {
        FileSystemResource resource = new FileSystemResource(filePath);
        mail.attachments.add(new MailResourcePair(name, resource));
        return this;
    }

    public MailBuilder attachment_classpath(String filePath) {
        ClassPathResource resource = new ClassPathResource(filePath);
        String name = resource.getFilename();
        mail.attachments.add(new MailResourcePair(name, resource));
        return this;
    }

    public MailBuilder message_classpath(String filePath) {
        ClassPathResource resource = new ClassPathResource(filePath);
        mail.messageResource = resource;
        return this;
    }

    public MailBuilder attachment_classpath(String filePath, String name) {
        ClassPathResource resource = new ClassPathResource(filePath);
        mail.attachments.add(new MailResourcePair(name, resource));
        return this;
    }

    public MailBuilder attachment_file(File file) {
        FileSystemResource resource = new FileSystemResource(file);
        mail.attachments.add(new MailResourcePair(file.getName(), resource));
        return this;
    }

    public MailBuilder attachment_file(File file, String name) {
        FileSystemResource resource = new FileSystemResource(file);
        mail.attachments.add(new MailResourcePair(name, resource));
        return this;
    }

    public MailBuilder message_file(File file) {
        FileSystemResource resource = new FileSystemResource(file);
        mail.messageResource = resource;
        return this;
    }

    public MailBuilder message_content(String messageContent) {
        InMemoryResource resource = new InMemoryResource(messageContent);
        mail.messageResource = resource;
        return this;
    }

    public MailBuilder message_content_type(String messageType) {
        mail.messageType = messageType;
        return this;
    }

    public MailBuilder use_thymeleaf() {
        mail.useThymeleaf = true;
        return this;
    }

    public MailBuilder thymeleaf_variable(String name, Object variable) {
        mail.variables.put(name, variable);
        return this;
    }

    public MailBuilder templateEngine(TemplateEngine engine) {
        mail.engine = engine;
        return this;
    }

    public MailBuilder message_charset(String charset){
         mail.charset = charset;
         return this;
    }
}
