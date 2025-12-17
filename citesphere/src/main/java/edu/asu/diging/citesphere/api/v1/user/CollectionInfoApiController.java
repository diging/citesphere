package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.user.IUser;

public class CollectionInfoApiController {
    
    @Autowired
    private ICitationCollectionManager collectionManager;

    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = { "/groups/{zoteroGroupId}/collections/{collectionId}/info" }, 
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ICitationCollection> getCollectionsInfoByGroupId(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value = "collectionId", required = true) String collectionId,
            Principal principal) {

        IUser user = userManager.findByUsername(principal.getName());

        ICitationCollection collection = collectionManager.getCollection(user, groupId, collectionId);
        return new ResponseEntity<ICitationCollection>(collection, HttpStatus.OK);
    }
}
