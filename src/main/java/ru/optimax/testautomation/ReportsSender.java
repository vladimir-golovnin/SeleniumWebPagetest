package ru.optimax.testautomation;

import freemarker.template.TemplateException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class ReportsSender {
    private static final String ENCODING = "UTF-8";
    private String subject = "Test results";
    private Properties mailSessionProperties;
    private final String from;
    private final String to;

    public ReportsSender(String propertiesFilename) throws FileNotFoundException {
        mailSessionProperties = new Properties();
        try (InputStream propertiesStream = this.getClass().getResourceAsStream(propertiesFilename)){
            mailSessionProperties.load(propertiesStream);
        } catch (IOException | NullPointerException e) {
            throw new FileNotFoundException("Can't open mail properties file");
        }
        from = mailSessionProperties.getProperty("from");
        to = mailSessionProperties.getProperty("to");
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public void sendReport(List<TestResult> results) throws MessagingException, FileNotFoundException {
        Session mailSession = Session.getDefaultInstance(mailSessionProperties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailSessionProperties.getProperty("login"),
                        mailSessionProperties.getProperty("pass"));
            }
        });

        MimeMessage msg = new MimeMessage(mailSession);

        msg.setFrom(from);
        msg.addRecipients(Message.RecipientType.TO, to);
        msg.setSubject(subject, ENCODING);
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        Map<String, Object> data = new TreeMap<>();
        data.put("results", results);
        String templateFile = mailSessionProperties.getProperty("template");
        String content;
        try {
            content = TemplateProcessor.processTemplate(templateFile, data);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new MessagingException("Unable to process message template.", e);
        }
        messageBodyPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart(messageBodyPart);

        for (TestResult result: results
             ) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                attachmentPart.attachFile(result.getScreenshot());
                attachmentPart.setContentID("<" + result.getScreenshot().getName() + ">");
                multipart.addBodyPart(attachmentPart);
            } catch (IOException e) {
                throw new FileNotFoundException("Unable to open screenshot for " + result.getUrl());
            }catch (NullPointerException e){

            }
        }
        msg.setContent(multipart);
        Transport.send(msg);

    }

}
