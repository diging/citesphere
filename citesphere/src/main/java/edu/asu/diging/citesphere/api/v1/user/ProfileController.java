package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.zotero.impl.ZoteroTokenManager;

@Controller
public class ProfileController extends V1Controller {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IJwtTokenService tokenService;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    @Autowired
    private IUploadJobManager jobManager;
    

    @RequestMapping(value="/user/zotero")
    public ResponseEntity<String> getProfile(@RequestParam("apitoken") String jwtToken) {
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
        node.put("zoteroId", zoteroToken.getUser().getZoteroId());
        
        return new ResponseEntity<>(node.toString(), HttpStatus.OK);
    }
}
