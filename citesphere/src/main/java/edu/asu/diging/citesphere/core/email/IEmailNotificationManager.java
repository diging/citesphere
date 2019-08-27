package edu.asu.diging.citesphere.core.email;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IEmailNotificationManager {

    public void sendNewAccountRequestPlacementEmail(IUser user, List<IUser> adminList);
}

