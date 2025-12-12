package edu.asu.diging.citesphere.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.api.v1.model.impl.Collections;
import edu.asu.diging.citesphere.api.v1.user.JsonUtil;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class GetCollectionListController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICitationCollectionManager collectionManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private JsonUtil jsonUtil;

    @RequestMapping(value = "/auth/import/collection/getgroupcollections", method = RequestMethod.GET)
    public ResponseEntity<Collections> getCollections( @RequestParam("groupId") String groupId, Authentication authentication) {
        IUser user = (IUser)authentication.getPrincipal();

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<Collections>(HttpStatus.NOT_FOUND);
        }

        Collections collectionResponse = new Collections();
        collectionResponse.setGroup(jsonUtil.createGroup(group));
        try {
            collectionResponse.setCollections(
                    collectionManager.getAllCollections(user, groupId, null, "title", 20));
        } catch (GroupDoesNotExistException e) {
            logger.error("Could not create job because group does not exist.", e);
            return new ResponseEntity<Collections>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Collections>(collectionResponse, HttpStatus.OK);
    }
}
