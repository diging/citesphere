package edu.asu.diging.citesphere.core.email.impl;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;

@Service
public class EmailNotificationSender {

    private final Logger logger = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.enabled}")
    private boolean enabled;

    @Value("${email.from}")
    private String fromAddress;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void sendNotificationEmail(IUser recepient, String subject, String body) {
        if (enabled && recepient.getEmail() != null && !recepient.getEmail().trim().isEmpty()) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(recepient.getEmail());
                helper.setSubject(subject);
                helper.setFrom(new InternetAddress(fromAddress));
                message.setContent(body, "text/html; charset=utf-8");
                mailSender.send(message);
                logger.info("The system sent an account request email to <<" + recepient.getEmail()
                        + ">> for the request placed by <<" + fromAddress + ">>.");

            } catch (MessagingException ex) {
                logger.error("Account request email could not be sent.", ex);
            }
        }
    }
}
