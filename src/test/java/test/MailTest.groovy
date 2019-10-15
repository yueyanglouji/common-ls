package test

import l.s.common.groovy.mail.MailService

class MailTest {
    static void main(String[] args){
        javatest();
        //grrovytest();

    }

    static void javatest(){
        l.s.common.mail.MailService s = new l.s.common.mail.MailService();
        //s.setSmtp_host('10.254.6.3')
        s.setSmtp_host('10.254.242.239')
        s.setSmtp_port('25')
        s.setDefault_from("KDC ASP GROUP <shoubudaoxin@kdc.benic.co.jp>")
        s.setDefault_content_type("text/plain;charset=UTF-8")

        s.send(
                s.getMailBuilder()
                //.to('li-xiaobao@kdc.benic.co.jp')
                .to('li-xiaobao@nwcs.co.jp')
                .subject('I\'m happy [[${test.defg}]]')
                .message_classpath("mailTemplate.html")
                .use_thymeleaf()
                .thymeleaf_variable("test", ["abc":"中华人民共和国", defg:"中国共产党"])
                .attachment_path('/Users/yueyanglouji/Desktop/款式-8.jpg')
                .attachment_classpath('mailTemplate.html')
                .build()
        )

    }

    static void grrovytest(){
        MailService s = new MailService();
        s.configure {
            smtp_host = '10.254.6.3'
            smtp_port = '25'
            default_from = "KDC ASP GROUP <shoubudaoxin@kdc.benic.co.jp>"
            default_content_type = "text/plain;charset=UTF-8"
        }

        s.send {
            //from 'sender@yourdomain.com'
            to 'li-xiaobao@kdc.benic.co.jp'
            //cc 'lixiaobao@nwcs.co.jp', 'li-xiaobao@nwcs.co.jp'
            //bcc 'lixiaobao@nwcs.co.jp', 'li-xiaobao@nwcs.co.jp'

            subject 'I\'m happy [[${test.defg}]]'

            message {
                //content "I'm very happy today! [[abc]]"
                //path "path"
                classpath "mailTemplate.html"
                //charset "UTF-8"

                //type 'text/plain'
                //contentType 'text/html;charset=UTF-8'
            }

            thymeleaf{
                variable "test", ["abc":"中华人民共和国", defg:"中国共产党"]
                //templateEngine engine
            }

            attachment {
                path '/Users/yueyanglouji/Desktop/款式-8.jpg'
                classpath 'mailTemplate.html'
            }
        }
    }
}
