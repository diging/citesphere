package edu.asu.diging.citesphere.web.user;

import java.util.HashSet;
import java.util.Set;

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
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IReference;
import edu.asu.diging.citesphere.model.bib.impl.Reference;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class UpdateItemController {
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationStore citationStore;
    
    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/update", method = RequestMethod.POST)
    public ResponseEntity<String> updateReference(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(value = "reference", required = false) String reference)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        try {
            ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
            Set<IReference> references = citation.getReferences();
            Boolean referenceExists = false;
            if (references == null) {
                references = new HashSet<>();
            } else {
                for (IReference refer : references) {
                    if (refer.getReferenceString().equals(reference)) {
                        referenceExists = true;
                        break;
                    }
                }
            }
            if (!referenceExists) {
                IReference iReference = new Reference();
                iReference.setReferenceString(reference);
                references.add(iReference);
            }
            citationStore.save(citation);
        } catch (Exception ex) {
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}

