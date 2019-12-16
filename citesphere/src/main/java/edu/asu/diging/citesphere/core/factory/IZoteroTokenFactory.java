package edu.asu.diging.citesphere.core.factory;

import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.model.IUser;

public interface IZoteroTokenFactory {

    IZoteroToken createZoteroToken(String token, String secret, String userId, IUser user);

}