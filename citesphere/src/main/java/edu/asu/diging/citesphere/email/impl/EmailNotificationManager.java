package edu.asu.diging.citesphere.email.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.email.IEmailNotificationManager;

@Service
public class EmailNotificationManager implements IEmailNotificationManager {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationManager.class);

    @Autowired
    @Qualifier(value = "appFile")
    private Properties appProperties;

    @Autowired
    private EmailNotificationSender emailNotificationSender;

    @Override
    public void sendNewAccountRequestPlacementEmail(IUser user, List<IUser> adminList) {
        if (user.getEmail() != null && !user.getEmail().equals("") && !adminList.isEmpty()) {
            InputStream resource = null;
            try {
                resource = new ClassPathResource("/newAccountNotification.txt").getInputStream();
            } catch (IOException e) {
                logger.error("Could not find email template for new account notification " + e);
            }
            String body = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
                body = reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                logger.error("Could not load email template for new account notification " + e);
            }
            body = body.replace("$createdUsername", user.getUsername());
            body = body.replace("$createdUser", user.getFirstName() + " " + user.getLastName());
            body = body.replace("$app", appProperties.getProperty("app.name"));
            body = body.replace("$url", appProperties.getProperty("app.url")
                    + appProperties.getProperty("app.createUserApprovalPath"));
            emailNotificationSender.sendNotificationEmail(user.getEmail(),
                    "New Account Request for " + appProperties.getProperty("app.name"), body,
                    adminList);
        }
    }
}
