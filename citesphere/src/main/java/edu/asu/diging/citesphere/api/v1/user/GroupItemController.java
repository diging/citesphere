package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.ICollectionResult;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.model.impl.CollectionResult;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;

@Controller
public class GroupItemController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
    @Value("${_available_item_columns}")
    private String availableColumns;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationCollectionManager collectionManager;

    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value = {"/auth/group/{zoteroGroupId}/collections/{collectionId}/items"})
    public ResponseEntity<String> getCollectionsByGroupId(@RequestHeader HttpHeaders headers, @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value = "collectionId", required = false) String collectionId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String[] columns)
            throws GroupDoesNotExistException {

        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
           logger.warn("Trying to access invalid page number: " + page);
        }

        // TODO: Get logged in user, remove:
        IUser user = userManager.findByUsername("namrathaov");
        CitationResults results = citationManager.getGroupItems(user, groupId, null, pageInt, sort);

        ICollectionResult collectionResponse = new CollectionResult();
        collectionResponse.setTotal(results.getTotalResults());
        collectionResponse.setTotalPages(Math.ceil(new Float(results.getTotalResults()) / new Float(zoteroPageSize)));
        collectionResponse.setCurrentPage(pageInt);
        collectionResponse.setZoteroGroupId(groupId);
        collectionResponse.setCollectionId(collectionId);
        collectionResponse.setCitationCollections(collectionManager.getCitationCollections(user, groupId, null, pageInt, sort)
                .getCitationCollections());
        List<ICitation> list = results.getCitations();
        collectionResponse.setItems(list);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(collectionResponse);
        } catch (IOException e) {
            logger.error("Unable to process JSON response ", e);
        }
        return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
    }
}
