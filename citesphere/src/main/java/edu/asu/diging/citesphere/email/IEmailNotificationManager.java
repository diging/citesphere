package edu.asu.diging.citesphere.email;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IEmailNotificationManager {

    public void sendNewAccountRequestPlacementEmail(IUser user);
}

