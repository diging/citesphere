package edu.asu.diging.citesphere.core.zotero.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.impl.ZoteroToken;
import edu.asu.diging.citesphere.core.repository.TokenRepository;
import edu.asu.diging.citesphere.core.zotero.IZoteroTokenManager;
import edu.asu.diging.citesphere.model.IUser;

@Service
public class ZoteroTokenManager implements IZoteroTokenManager {
    
    @Autowired
    private TokenRepository tokenRepo;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IZoteroTokenManager#getToken(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public IZoteroToken getToken(IUser user) {
        return (IZoteroToken) tokenRepo.findByUserUsername(user.getUsername());
    }
    
    @Override
    public IZoteroToken store(IZoteroToken token) {
        return (IZoteroToken) tokenRepo.save((ZoteroToken)token);
    }
}
