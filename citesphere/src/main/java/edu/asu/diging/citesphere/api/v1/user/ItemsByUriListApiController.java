package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.Items;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Controller
@PropertySource("classpath:/config.properties")
public class ItemsByUriListApiController extends V1Controller {

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

    @RequestMapping(value = { "/groups/items" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getItemsByUri(@RequestHeader HttpHeaders headers,
            @RequestParam(defaultValue = "1", required = false, value = "page") int page,
            @RequestParam(value = "uri") String uri, Principal principal) {

        IUser user = userManager.findByUsername(principal.getName());

        CitationResults results = citationManager.getItemsByUri(user, uri, page);
        Items itemsResponse = new Items();
        itemsResponse.setItems(results.getCitations());

        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(itemsResponse);
        } catch (IOException e) {
            logger.error("Unable to process JSON response ", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
    }
}
