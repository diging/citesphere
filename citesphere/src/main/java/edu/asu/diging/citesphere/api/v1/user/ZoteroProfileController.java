package edu.asu.diging.citesphere.api.v1.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.CitesphereHeaders;
import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.zotero.impl.ZoteroTokenManager;

@Controller
public class ZoteroProfileController extends V1Controller {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IJwtTokenService tokenService;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    @Autowired
    private IUploadJobManager jobManager;
    

    @RequestMapping(value="/user/zotero")
    public ResponseEntity<String> getProfile(@RequestHeader HttpHeaders headers) {
        List<String> tokenHeader = headers.get(CitesphereHeaders.CITESPHERE_API_TOKEN);
        if (tokenHeader == null || tokenHeader.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "No token provided.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.BAD_REQUEST);
        }
        // there should only be one
        String jwtToken = tokenHeader.get(0);
        IJobApiTokenContents tokenContents = tokenService.getJobApiTokenContents(jwtToken);
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
        IUploadJob job = jobManager.findUploadJob(tokenContents.getJobId());
        IZoteroToken zoteroToken = tokenManager.getToken(userManager.findByUsername(job.getUsername()));
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("zotero", zoteroToken.getToken());
        node.put("zoteroId", zoteroToken.getUserId());
        
        return new ResponseEntity<>(node.toString(), HttpStatus.OK);
    }
}
