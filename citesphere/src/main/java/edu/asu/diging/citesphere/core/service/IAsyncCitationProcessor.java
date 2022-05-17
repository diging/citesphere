package edu.asu.diging.citesphere.core.service;

import java.util.Map;
import java.util.concurrent.Future;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.user.IUser;

public interface IAsyncCitationProcessor {

    Future<String> sync(IUser user, String groupId, long contentVersion, String collectionId) throws ZoteroHttpStatusException;

}