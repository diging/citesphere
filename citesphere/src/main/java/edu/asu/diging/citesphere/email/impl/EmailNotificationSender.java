package edu.asu.diging.citesphere.email.impl;

import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import edu.asu.diging.citesphere.core.model.IUser;

public class EmailNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Autowired
    private JavaMailSender mailSender;
    
    private boolean enabled = false;

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
    
    public void sendNotificationEmail(String emailaddress, String subject, String msgText, List<IUser> adminList) {
        if (enabled) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                for(IUser to: adminList) {
                    helper.addTo(to.getEmail());
                }
                helper.setSubject(subject);
                helper.setFrom(new InternetAddress(fromAddress));
                helper.setText(msgText);
                mailSender.send(message);
                logger.debug("Send email to admin with subject \"" + subject + "\"");
            } catch (MessagingException ex) {
                logger.error("Notification email could not be sent.", ex);
            }
        }
    }
}
