package edu.asu.diging.citesphere.api.v1.upload;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;

@Controller
public class RetrieveUploadController extends V1Controller {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IJwtTokenService tokenService;
    
    @Autowired
    private IUploadJobManager jobManager;
    
    
    @RequestMapping(value="/upload")
    public ResponseEntity<String> get(HttpServletResponse response, @RequestHeader HttpHeaders headers) {
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
        
        byte[] content = jobManager.getUploadedFile(job);
        if (content == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "No content.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.I_AM_A_TEAPOT);
        }
        response.setContentType(job.getContentType());
        response.setContentLength(new Long(job.getFileSize()).intValue());
        response.setHeader("Content-disposition", "filename=\"" + job.getFilename() + "\""); 
        try {
            response.getOutputStream().write(content);
            response.getOutputStream().close();
        } catch (IOException e) {
            logger.error("Could not create repsonse object.", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
