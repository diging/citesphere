package edu.asu.diging.citesphere.core.service;

import edu.asu.diging.citesphere.user.IUser;

public interface IAsyncCitationProcessor {

    void sync(IUser user, String groupId, long contentVersion, String collectionId);

}