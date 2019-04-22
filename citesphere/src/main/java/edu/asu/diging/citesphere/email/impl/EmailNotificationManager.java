package edu.asu.diging.citesphere.email.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.email.IEmailNotificationManager;
import edu.asu.diging.citesphere.email.impl.EmailNotificationManager;
import edu.asu.diging.citesphere.email.impl.EmailNotificationSender;

@Service
public class EmailNotificationManager implements IEmailNotificationManager {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationManager.class);

    @Autowired
    private EmailNotificationSender emailNotificationSender;
    
    @Override
    public void sendNewAccountRequestPlacementEmail(IUser admin) {
        if (admin.getEmail() != null && !admin.getEmail().equals("")) {
            InputStream resource = null;
            try {
                resource = new ClassPathResource("/newAccountNotification.txt").getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String body = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
                body = reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            body = body.replace("$user", admin.getFirstName() + " " + admin.getLastName());
            body = body.replace("$app", "Citesphere");
            
            emailNotificationSender.sendNotificationEmail(admin.getEmail(), "New Account Request", body);
            logger.info("The system sent a user request email to <<" + admin.getUsername()
                    + ">> for the request placed.");
        }

    }
}