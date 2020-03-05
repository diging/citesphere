package edu.asu.diging.citesphere.api.v1.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.CitesphereHeaders;
import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;

public class BaseJobInfoController extends V1Controller {

    @Autowired
    private IJwtTokenService tokenService;

    protected ResponseEntity<String> checkForToken(HttpHeaders headers) {
        List<String> tokenHeader = headers.get(CitesphereHeaders.CITESPHERE_API_TOKEN);
        if (tokenHeader == null || tokenHeader.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "No token provided.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    protected IJobApiTokenContents getTokenContents(HttpHeaders headers) {
        List<String> tokenHeader = headers.get(CitesphereHeaders.CITESPHERE_API_TOKEN);
        // there should only be one
        String jwtToken = tokenHeader.get(0);
        return tokenService.getJobApiTokenContents(jwtToken);
    }
    
    protected ResponseEntity<String> checkTokenValidity(IJobApiTokenContents tokenContents) {
        if (tokenContents == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Token signature is invalid.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.UNAUTHORIZED);
        }
        if (tokenContents.isExpired()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Token has expired.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.UNAUTHORIZED);
        }
        return null;
    }
}
