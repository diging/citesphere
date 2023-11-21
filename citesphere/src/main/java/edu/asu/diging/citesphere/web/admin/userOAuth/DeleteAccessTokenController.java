package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;

public class DeleteAccessTokenController {
    @Autowired
    private IUserTokenManager userTokenManager;
    
    @RequestMapping(value = "/admin/user/accessTokens/{accessTokenId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteApp(@PathVariable("accessTokenId") String accessTokenId) {
        userTokenManager.deleteClient(accessTokenId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
