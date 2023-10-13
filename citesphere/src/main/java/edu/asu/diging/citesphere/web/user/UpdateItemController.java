package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class UpdateItemController {
    
    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/update", method = RequestMethod.POST)
    public ResponseEntity<ICitation> updateReference(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(value = "reference", required = false) String reference)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
        citation = citationManager.updateCitationReference(citation, reference);
        return new ResponseEntity<ICitation>(citation, HttpStatus.OK);
    }
}

