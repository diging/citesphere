package edu.asu.diging.citesphere.web.upload;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.core.model.impl.User;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;

@Controller
public class ImportReferencesController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUploadJobManager jobManager;
    
    @RequestMapping(value = "/auth/import/upload", method = RequestMethod.GET)
    public String show(Model model) {
        return "auth/import/upload";
    }

    @RequestMapping(value = "/auth/import/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFiles(Principal principal,
            @RequestParam("files") MultipartFile[] files) {

        User user = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            user = (User) ((UsernamePasswordAuthenticationToken) principal)
                    .getPrincipal();
        }
        
        if (user == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

        List<byte[]> fileBytes = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileBytes.add(file.getBytes());
            } catch (IOException e) {
                logger.error("Could not get file content from request.", e);
                fileBytes.add(null);
            }
        }
        List<IUploadJob> jobs = jobManager.createUploadJob(user, files, fileBytes);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode filesNode = root.putArray("jobs");
        for (IUploadJob job : jobs) {
            ObjectNode jobNode = mapper.createObjectNode();
            jobNode.put("jobId", job.getId());
            jobNode.put("filename", job.getFilename());
            jobNode.put("status", job.getStatus().name());
            filesNode.add(jobNode);
        }
        return new ResponseEntity<String>(root.toString(), HttpStatus.OK);
    }

}
