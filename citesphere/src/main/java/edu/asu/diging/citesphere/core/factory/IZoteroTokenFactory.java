package edu.asu.diging.citesphere.core.factory;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;

public interface IZoteroTokenFactory {

    IZoteroToken createZoteroToken(String token, String secret, String userId, IUser user);

}