package l.s.common.groovy.mail;

import l.s.common.thymeleaf.Thymeleaf;
import l.s.common.thymeleaf.ThymeleafExecutor;
import l.s.common.thymeleaf.ThymeleafFactory;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.File;

public class Mail {

    String from = null;
    List<String> to = new ArrayList<>();
    List<String> cc = new ArrayList<>();
    List<String> bcc = new ArrayList<>();
    List<MailResourcePair> attachments = new ArrayList<>();
    String subject = "";
    Resource messageResource = null;
    String charset = null;
    String messageType = null;

    boolean useThymeleaf = false;
    Map<String, Object> variables = new HashMap<>();
    TemplateEngine engine = null;
    Thymeleaf thymeleaf = null;

    private Multipart createMessageBody() throws Exception{
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setHeader("Content-Type", messageType);

        StringBuilder content = new StringBuilder();
        if(this.messageResource != null){

            if(this.messageResource.getClass() == InMemoryResource.class){
                this.charset = "UTF-8";
            }
            try(
                InputStream in = this.messageResource.getInputStream();
                InputStreamReader reader = new InputStreamReader(in, charset);
                Scanner sc = new Scanner(reader)
            ){
                while(sc.hasNextLine()){
                    String line = sc.nextLine();
                    content.append(line);
                    content.append("\n");
                }
            }
        }

        if(this.useThymeleaf){
            ThymeleafExecutor executor = thymeleaf.createExecutor();
            for(Map.Entry<String, Object> en : this.variables.entrySet()){
                String name = en.getKey();
                Object variable = en.getValue();
                executor.setVariable(name, variable);
            }
            bodyPart.setContent(executor.process(content.toString()), messageType);
        }else{
            bodyPart.setContent(content.toString(), messageType);
        }

        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(bodyPart);

        if (attachments.size() > 0) {
            for(MailResourcePair pair : attachments){
                Resource resource = pair.getResource();
                String name = pair.getName();
                bodyPart = new MimeBodyPart();
                addAttachement(bodyPart, multiPart, resource, name);
            }
        }

        return multiPart;
    }

    private void addAttachement(MimeBodyPart bodyPart, Multipart multiPart, Resource attachment, String attachmentName) throws Exception{
        DataSource source = new ResourceDataSource(attachment);
        bodyPart.setDataHandler(new DataHandler(source));
        bodyPart.setFileName(MimeUtility.encodeWord(attachmentName));
        multiPart.addBodyPart(bodyPart);
    }

    private void createRecipients(MimeMessage message) throws Exception{
        if (to.size() > 0) {
            setRecipients(message, Message.RecipientType.TO, to);
        }
        if (cc.size() > 0) {
            setRecipients(message, Message.RecipientType.CC, cc);
        }
        if (bcc.size() > 0) {
            setRecipients(message, Message.RecipientType.BCC, bcc);
        }
    }

    public void setRecipients(MimeMessage message, RecipientType recipientType, List<String> recipients) throws Exception{
        List<InternetAddress> list = new ArrayList<>();
        for(String it : recipients){
            list.add(new InternetAddress(it));
        }
        message.setRecipients(recipientType, list.toArray(new Address[list.size()]));
    }

    private void initThymeleaf(){
        if(engine != null){
            thymeleaf = ThymeleafFactory.getEngineExistsThymeleaf(engine);
        }else{
            thymeleaf = ThymeleafFactory.getStringContentThymeleaf(TemplateMode.HTML);
        }
    }

    private MimeMessage constructMail(Session session) throws Exception{

        if(this.useThymeleaf){
            initThymeleaf();
        }

        MimeMessage mimeMessage = new MimeMessage(session);
        createRecipients(mimeMessage);

        mimeMessage.setFrom(new InternetAddress(from));

        if(this.useThymeleaf){
            ThymeleafExecutor executor = thymeleaf.createExecutor();
            for(Map.Entry<String, Object> en : this.variables.entrySet()){
                String name = en.getKey();
                Object variable = en.getValue();
                executor.setVariable(name, variable);
            }
            mimeMessage.setSubject(executor.process(subject));
        }else{
            mimeMessage.setSubject(subject);
        }
        mimeMessage.setContent(createMessageBody());
        mimeMessage.setSentDate(new Date());
        mimeMessage.saveChanges();

        return mimeMessage;
    }

    public void send(Session session) throws Exception{
        Transport.send(constructMail(session));
    }

}
