package edu.asu.diging.citesphere.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class UpdateItemController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateReference(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(value = "referenceCitationKey") String referenceCitationKey,
            @RequestParam(value = "reference") String reference) {
        try {
            ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
            citation = citationManager.updateCitationReference(citation, referenceCitationKey, reference);
            return new ResponseEntity<>(citation, HttpStatus.OK);
        } catch (GroupDoesNotExistException e) {
            logger.error("Group does not exist.", e);
            return new ResponseEntity<>("{\"error\": \"Group " + zoteroGroupId + " not found.\"}", HttpStatus.NOT_FOUND);
        } catch (CannotFindCitationException e) {
            logger.error("Citation not found.", e);
            return new ResponseEntity<>("{\"error\": \"Item " + itemId + " not found.\"}", HttpStatus.NOT_FOUND);
        } catch (ZoteroHttpStatusException e) {
            logger.error("Zotero threw exception.", e);
            return new ResponseEntity<>("{\"error\": \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
