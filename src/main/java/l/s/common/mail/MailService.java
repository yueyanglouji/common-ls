package l.s.common.mail;

import javax.mail.Session;
import java.util.Properties;

public class MailService {

    public String smtp_host;
    public String smtp_port;
    private String transport_type = "smtp";
    private Properties PROPERTIES;

    private String default_from = "common-ls@52ove.com";
    private String default_content_type = "text/plain;charset=UTF-8";
    private String default_charset = "UTF-8";

    private void initProps(){
        PROPERTIES = new Properties();
        PROPERTIES.put("mail.smtp.host", smtp_host);
        PROPERTIES.put("mail.smtp.port", smtp_port);
        PROPERTIES.put("mail.transport.protocol", transport_type);
    }

    public Session getSession() {
        if(PROPERTIES == null){
            initProps();
        }

        return Session.getDefaultInstance(PROPERTIES);
    }

    public MailBuilder getMailBuilder(){
        return new MailBuilder(this);
    }

    public void send(Mail mail) throws Exception{
        mail.send(getSession());
    }

    public String getSmtp_host() {
        return smtp_host;
    }

    public void setSmtp_host(String smtp_host) {
        this.smtp_host = smtp_host;
    }

    public String getSmtp_port() {
        return smtp_port;
    }

    public void setSmtp_port(String smtp_port) {
        this.smtp_port = smtp_port;
    }

    public String getTransport_type() {
        return transport_type;
    }

    public void setTransport_type(String transport_type) {
        this.transport_type = transport_type;
    }

    public String getDefault_from() {
        return default_from;
    }

    public void setDefault_from(String default_from) {
        this.default_from = default_from;
    }

    public String getDefault_content_type() {
        return default_content_type;
    }

    public void setDefault_content_type(String default_content_type) {
        this.default_content_type = default_content_type;
    }

    public String getDefault_charset() {
        return default_charset;
    }

    public void setDefault_charset(String default_charset) {
        this.default_charset = default_charset;
    }
}
