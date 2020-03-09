package edu.asu.diging.citesphere.core.email;

import java.io.IOException;
import java.util.List;

import edu.asu.diging.citesphere.core.model.IPasswordResetToken;
import edu.asu.diging.citesphere.user.IUser;

public interface IEmailNotificationManager {

    public void sendNewAccountRequestPlacementEmail(IUser user, List<IUser> adminList);

    void sendResetPasswordEmail(IUser user, IPasswordResetToken token) throws IOException;
}

