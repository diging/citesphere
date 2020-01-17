package edu.asu.diging.citesphere.config.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.web.context.request.NativeWebRequest;

import edu.asu.diging.citesphere.core.factory.IZoteroTokenFactory;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.zotero.IZoteroTokenManager;
import edu.asu.diging.citesphere.model.IUser;

public final class SimpleSignInAdapter implements SignInAdapter {
    
    private IZoteroTokenManager tokenManager;
    private IZoteroTokenFactory tokenFactory;
    
    public SimpleSignInAdapter(IZoteroTokenManager manager, IZoteroTokenFactory tokenFactory) {
        this.tokenManager = manager;
        this.tokenFactory = tokenFactory;
    }

    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String accessToken = connection.createData().getAccessToken();
            String secret = connection.createData().getSecret();
            String zoteroUserId = ((Zotero)connection.getApi()).getUserId();
            IUser user = (IUser) authentication.getPrincipal();
            
            IZoteroToken token = tokenFactory.createZoteroToken(accessToken, secret, zoteroUserId, user);
            tokenManager.store(token);
        }
        
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(
                request.getNativeRequest(HttpServletRequest.class),
                request.getNativeResponse(HttpServletResponse.class));

        if (savedRequest != null) {
            return savedRequest.getRedirectUrl();
        }
        return null;
    }

}