package edu.asu.diging.citesphere.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.service.impl.DbTokenStore;
import edu.asu.diging.citesphere.core.service.oauth.impl.OAuthClientManager;

@Controller
public class OAuthAppsRevokeTokenController {

    @Autowired
    private DbTokenStore tokenStore;
    
    @Autowired
    OAuthClientManager clientManager;

    @RequestMapping(value="/tokens/{clientId}", method=RequestMethod.DELETE)
    public ResponseEntity<String> revokeAccessForApp(@PathVariable("clientId") String clientId) {
        tokenStore.revokeAccessToken(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }    
}
