package com.cocs.mail;

import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.cocs.common.Env;

@Component
public class MailSender {
 
    @Autowired
    private VelocityEngine velocityEngine;
 
    private static final boolean DEBUG = false;
    
    public void send(final Template template, final String toEmailAddresses, Map<String, Object> prop) throws Throwable {
    	
    	String fromEmailAddress = Env.getProperty("smtp.senderName");
    	String subject = template.getSubject();
    	String location = template.getLocation();
    	String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, location, "UTF-8", prop);
    	
        send(toEmailAddresses, fromEmailAddress, subject, content);
    }
    
    public void send(final String toEmailAddresses, final String subject, final String content) throws Throwable {
    	
    	String fromEmailAddress = Env.getProperty("smtp.senderName");
    	
        send(toEmailAddresses, fromEmailAddress, subject, content);
    }
 
    private void send(final String toEmailAddresses, final String fromEmailAddress,
                           final String subject, String content) {
 
		Properties props = new Properties();
		props.put("mail.smtp.host", Env.getProperty("smtp.address"));
		props.put("mail.smtp.port", Env.getProperty("smtp.port"));
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", DEBUG);
		
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Env.getProperty("smtp.userId"), Env.getProperty("smtp.password"));
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmailAddress));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddresses));
			message.setSubject(subject);
			message.setContent(content, "text/html; charset=utf-8");
			
			Transport.send(message);
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
    }
}