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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.Items;
import edu.asu.diging.citesphere.api.v1.model.impl.SyncInfo;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Controller
@PropertySource("classpath:/config.properties")
public class ItemsApiController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_api_page_size}")
    private Integer apiPageSize;

    @Value("${_available_item_columns}")
    private String availableColumns;

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private JsonUtil jsonUtil;
    
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = { "/groups/{zoteroGroupId}/items",
            "/groups/{zoteroGroupId}/collections/{collectionId}/items" }, produces = {
                    MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getCollectionsByGroupId(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value = "collectionId", required = false) String collectionId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String[] columns, Principal principal)
            throws GroupDoesNotExistException {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        
        // TODO: Get logged in user, remove:
        IUser user = userManager.findByUsername(principal.getName());
        
        ICitationGroup group =groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        CitationResults results;
        try {
            results = citationManager.getGroupItems(user, groupId, collectionId, pageInt, sort);
        } catch(AccessForbiddenException ex) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        } catch (ZoteroHttpStatusException e1) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Items itemsResponse = new Items();
        itemsResponse.setGroup(jsonUtil.createGroup(group));
        itemsResponse.getGroup().setSyncInfo(getSyncInfo(group));
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
