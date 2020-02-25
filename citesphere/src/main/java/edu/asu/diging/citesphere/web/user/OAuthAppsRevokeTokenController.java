package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.service.impl.DbTokenStore;

@Controller
public class OAuthAppsRevokeTokenController {

    @Autowired
    private DbTokenStore tokenStore;

    @RequestMapping(value="/auth/tokens/{clientId}", method=RequestMethod.DELETE)
    public ResponseEntity<String> revokeAccessForApp(Authentication authentication, @PathVariable("clientId") String clientId) {
        tokenStore.revokeAccessToken(clientId, authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }    
}
