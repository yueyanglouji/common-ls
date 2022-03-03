package l.s.common.groovy.mail;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MailBuilder extends GroovyObjectSupport {

    private Mail mail;

    private boolean toMode = false;
    private boolean ccMode = false;
    private boolean bccMode = false;
    private boolean attachmentMode = false;
    private boolean messageMode = false;
    private boolean thymeleafMode = false;

    public Mail build(MailService service, Closure definition) {
        mail = new Mail();
        mail.charset = service.getDefault_charset();
        mail.messageType = service.getDefault_content_type();
        mail.from = service.getDefault_from();

        runClosure(definition);
        return mail;
    }

    public void to(Closure toClosure) {
        toMode = true;
        runClosure(toClosure);
        toMode = false;
    }

    public void to(String... tos) {
        to(Arrays.asList(tos));
    }

    public void to(List<String> tos) {
        toMode = true;
        if(tos == null || tos.size() == 0){
            throw new RuntimeException("to set null");
        }
        for(String to : tos){
            mail.to.add(to);
        }
        toMode = false;
    }

    public void cc(Closure ccClosure) {
        ccMode = true;
        runClosure(ccClosure);
        ccMode = false;
    }

    public void cc(String... ccs) {
        cc(Arrays.asList(ccs));
    }

    public void cc(List<String> ccs) {
        toMode = true;
        if(ccs == null || ccs.size() == 0){
            throw new RuntimeException("cc set null");
        }
        for(String cc : ccs){
            mail.cc.add(cc);
        }
        toMode = false;
    }

    public void bcc(Closure bccClosure) {
        bccMode = true;
        runClosure(bccClosure);
        bccMode = false;
    }

    public void bcc(String... bccs) {
        bcc(Arrays.asList(bccs));
    }

    public void bcc(List<String> bccs) {
        toMode = true;
        if(bccs == null || bccs.size() == 0){
            throw new RuntimeException("bcc set null");
        }
        for(String bcc : bccs){
            mail.bcc.add(bcc);
        }
        toMode = false;
    }

    public void email(String email) {
        if (toMode) {
            mail.to.add(email);
        } else if (ccMode) {
            mail.cc.add(email);
        } else if (bccMode) {
            mail.bcc.add(email);
        } else {
            throw new UnsupportedOperationException("email() only allowed in to, cc or bcc context.");
        }
    }

    public void from(String from){
        mail.from = from;
    }

    public void subject(String subject){
        mail.subject = subject;
    }

    public void attachment(Closure closure) {
        attachmentMode = true;
        runClosure(closure);
        attachmentMode = false;
    }

    public void path(String filePath) {
        if (attachmentMode) {
            FileSystemResource resource = new FileSystemResource(filePath);
            String name = resource.getFilename();
            mail.attachments.add(new MailResourcePair(name, resource));
        }
        else if(messageMode){
            FileSystemResource resource = new FileSystemResource(filePath);
            mail.messageResource = resource;
        }
        else {
            throw new UnsupportedOperationException("path() only allowed in attachment or message context.");
        }
    }

    public void path(String filePath, String name) {
        if (attachmentMode) {
            FileSystemResource resource = new FileSystemResource(filePath);
            mail.attachments.add(new MailResourcePair(name, resource));
        }
        else if(messageMode){
            FileSystemResource resource = new FileSystemResource(filePath);
            mail.messageResource = resource;
        }
        else {
            throw new UnsupportedOperationException("path() only allowed in attachment or message context.");
        }
    }

    public void classpath(String filePath){
        if (attachmentMode) {
            ClassPathResource resource = new ClassPathResource(filePath);
            String name = resource.getFilename();
            mail.attachments.add(new MailResourcePair(name, resource));
        }
        else if(messageMode){
            ClassPathResource resource = new ClassPathResource(filePath);
            mail.messageResource = resource;
        }
        else {
            throw new UnsupportedOperationException("classpath() only allowed in attachment or message context.");
        }
    }

    public void classpath(String filePath, String name){
        if (attachmentMode) {
            ClassPathResource resource = new ClassPathResource(filePath);
            mail.attachments.add(new MailResourcePair(name, resource));
        }
        else if(messageMode){
            ClassPathResource resource = new ClassPathResource(filePath);
            mail.messageResource = resource;
        }
        else {
            throw new UnsupportedOperationException("classpath() only allowed in attachment or message context.");
        }
    }

    public void file(File file) {
        file(file, file.getName());
    }

    public void file(File file, String name) {
        if (attachmentMode) {
            FileSystemResource resource = new FileSystemResource(file);
            mail.attachments.add(new MailResourcePair(name, resource));
        }
        else if(messageMode){
            FileSystemResource resource = new FileSystemResource(file);
            mail.messageResource = resource;
        }
        else {
            throw new UnsupportedOperationException("file() only allowed in attachment context.");
        }
    }

    public void message(Closure messageClosure) {
        messageMode = true;
        runClosure(messageClosure);
        messageMode = false;
    }

    public void content(String messageContent) {
        if (messageMode) {
            InMemoryResource resource = new InMemoryResource(messageContent);
            mail.messageResource = resource;
        } else {
            throw new UnsupportedOperationException("content() only allowed in message context.");
        }
    }

    public void content_type(String messageType) {
        if (messageMode) {
            mail.messageType = messageType;
        } else {
            throw new UnsupportedOperationException("content_type() only allowed in message context.");
        }
    }

    public void thymeleaf(Closure thymeleafClosure) {
        thymeleafMode = true;

        mail.useThymeleaf = true;
        runClosure(thymeleafClosure);

        thymeleafMode = false;
    }

    public void variable(String name, Object variable) {
        if (thymeleafMode) {
            mail.variables.put(name, variable);
        } else {
            throw new UnsupportedOperationException("variable() only allowed in thymeleaf context.");
        }
    }

    public void templateEngine(TemplateEngine engine) {
        if (thymeleafMode) {
            mail.engine = engine;
        } else {
            throw new UnsupportedOperationException("variable() only allowed in thymeleaf context.");
        }
    }

    public void charset(String charset){
        if (messageMode) {
            mail.charset = charset;
        } else {
            throw new UnsupportedOperationException("charset() only allowed in message context.");
        }
    }

    private void runClosure(Closure closure) {
        Closure runClone = (Closure) closure.clone();
        runClone.setDelegate(this);
        runClone.setResolveStrategy(Closure.DELEGATE_ONLY);
        runClone.call();
    }
}
