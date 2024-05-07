package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class CitationsApiController extends V1Controller {
	
	  private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_api_page_size}")
    private Integer apiPageSize;

    @Value("${_available_item_columns}")
    private String availableColumns;

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ObjectMapper objectMapper;
	
	@RequestMapping(value = {"/citations/search"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getCitationsByContributorUri(@RequestParam(defaultValue = "0", required = false, value = "page") long page,
            @RequestParam(defaultValue = "20", required = false, value = "pageSize") int pageSize,
            @RequestParam(value = "uri") String uri, Principal principal){
    	
    	IUser user = userManager.findByUsername(principal.getName());
    	List<ICitationGroup> groups = citationManager.getGroups(user);
    	
    	List<String> groupIds = groups.stream()
    		    .map(group -> String.valueOf(group.getGroupId()))
    		    .collect(Collectors.toList());
    	
    	CitationResults results = citationManager.getCitationsByContributorUri(groupIds, page, pageSize, uri);
    	
    	String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(results.getCitations());
        } catch (IOException e) {
            logger.error("Unable to process JSON response ", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
    }

}
