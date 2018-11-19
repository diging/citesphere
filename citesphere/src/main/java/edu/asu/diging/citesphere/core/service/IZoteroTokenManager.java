package edu.asu.diging.citesphere.core.service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;

public interface IZoteroTokenManager {

    IZoteroToken getToken(IUser user);

    IZoteroToken store(IZoteroToken token);

}