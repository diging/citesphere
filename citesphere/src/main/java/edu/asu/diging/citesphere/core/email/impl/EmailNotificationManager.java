package edu.asu.diging.citesphere.core.email.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.email.IEmailNotificationManager;
import edu.asu.diging.citesphere.core.model.IPasswordResetToken;
import edu.asu.diging.citesphere.model.IUser;

@Service
public class EmailNotificationManager implements IEmailNotificationManager {

    private final Logger logger = LoggerFactory.getLogger(EmailNotificationManager.class);

    @Autowired
    @Qualifier(value = "appFile")
    private Properties appProperties;

    @Autowired
    private EmailNotificationSender emailNotificationSender;

    @Override
    public void sendNewAccountRequestPlacementEmail(IUser user, List<IUser> adminList) {
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && !adminList.isEmpty()) {
            ClassPathResource resource = new ClassPathResource("/newAccountNotification.txt");

            String body;
            try {
                body = FileUtils.readFileToString(resource.getFile(), Charset.forName("utf-8"));
            } catch (IOException e) {
                logger.error("Could not read email template for new account notification.", e);
                return;
            }

            body = body.replace("$createdUsername", user.getUsername());
            body = body.replace("$createdUser", user.getFirstName() + " " + user.getLastName());
            body = body.replace("$app", appProperties.getProperty("app.name"));
            String appUrl = appProperties.getProperty("app.url");
            if (!appUrl.endsWith("/")) {
                appUrl += "/";
            }
            body = body.replace("$url", appUrl + appProperties.getProperty("app.createUserApprovalPath"));

            for (IUser admin : adminList) {
                // ugly but ahh well
                List<String> names = new ArrayList<>();
                if (admin.getFirstName() != null && !admin.getFirstName().trim().isEmpty()) {
                    names.add(admin.getFirstName());
                }
                if (admin.getLastName() != null && !admin.getLastName().trim().isEmpty()) {
                    names.add(admin.getLastName());
                }
                String name = String.join(" ", names);

                String message = body.replace("$admin", name.trim().isEmpty() ? admin.getEmail() : name);
                emailNotificationSender.sendNotificationEmail(admin,
                        "New Account Request for " + appProperties.getProperty("app.name"), message);
            }
        }
    }

    @Override
    public void sendResetPasswordEmail(IUser user, IPasswordResetToken token) throws IOException {
        ClassPathResource resource = new ClassPathResource("/passwordResetEmail.txt");

        String body;
        try {
            body = FileUtils.readFileToString(resource.getFile(), Charset.forName("utf-8"));
        } catch (IOException e) {
            logger.error("Could not read email template for new account notification.", e);
            return;
        }

        body = body.replace("$user", user.getFirstName() + " " + user.getLastName());
        body = body.replace("$app", appProperties.getProperty("app.name"));

        String appUrl = appProperties.getProperty("app.url");
        if (!appUrl.endsWith("/")) {
            appUrl += "/";
        }
        body = body.replace("$url", appUrl + appProperties.getProperty("app.createUserApprovalPath") + "?token="
                + token.getToken() + "&user=" + user.getUsername());

        emailNotificationSender.sendNotificationEmail(user, appProperties.getProperty("app.resetPasswordSubject")
                .replace("$app", appProperties.getProperty("app.name")), body);
    }

}
