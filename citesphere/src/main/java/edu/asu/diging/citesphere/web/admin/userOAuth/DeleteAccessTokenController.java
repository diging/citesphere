package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;

@Controller
public class DeleteAccessTokenController {
    @Autowired
    private IUserTokenManager userTokenManager;
    
    @Autowired
    private IOAuthClientManager clientManager;
    
    @RequestMapping(value = "/admin/user/auth/accessTokens/{accessTokenId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteApp(@PathVariable("accessTokenId") String accessTokenId) {
        clientManager.deleteClient(accessTokenId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
