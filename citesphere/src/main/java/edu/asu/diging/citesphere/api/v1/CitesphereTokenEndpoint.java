package edu.asu.diging.citesphere.api.v1;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public class CitesphereTokenEndpoint extends TokenEndpoint {

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> postAccessToken(Principal principal,
            @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return super.postAccessToken(principal, parameters);
    }

}
