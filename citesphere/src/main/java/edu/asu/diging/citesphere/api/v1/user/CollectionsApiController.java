package edu.asu.diging.citesphere.api.v1.user;

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

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.Collections;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
@PropertySource("classpath:/config.properties")
public class CollectionsApiController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_api_page_size}")
    private Integer apiPageSize;

    @Value("${_available_item_columns}")
    private String availableColumns;

    @Autowired
    private ICitationCollectionManager collectionManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    

    @Autowired
    private JsonUtil jsonUtil;

    @RequestMapping(value = { "/groups/{zoteroGroupId}/collections",
            "/groups/{zoteroGroupId}/collections/{collectionId}/collections" }, produces = {
                    MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collections> getCollectionsByGroupId(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value = "collectionId", required = false) String collectionId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "20", required = false, value = "maxCollectionNumber") String maxCollections,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort, Principal principal)
            throws GroupDoesNotExistException {
        // TODO add pagination
        
        IUser user = userManager.findByUsername(principal.getName());

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<Collections>(HttpStatus.NOT_FOUND);
        }

        Collections collectionResponse = new Collections();
        collectionResponse.setGroup(jsonUtil.createGroup(group));
        collectionResponse.getGroup().setSyncInfo(getSyncInfo(group));
        collectionResponse.setCollections(
                collectionManager.getAllCollections(user, groupId, collectionId, sort, new Integer(maxCollections)));
        return new ResponseEntity<Collections>(collectionResponse, HttpStatus.OK);
    }

}
