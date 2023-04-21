package edu.asu.diging.citesphere.web.user;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationStore citationStore;
    
//    @RequestParam(required = false, value = "reference" String reference,
    
    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/update", method = RequestMethod.POST)
    public ResponseEntity<String> resolveConflict(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestBody String reference)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        try {
            ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId,
                    itemId);
           
            Set<IReference> references = citation.getReferences();
            if (references == null) {
                references = new HashSet<>();
            }
            IReference refer = new Reference();
            refer.setReferenceString(reference);
            references.add(refer);
            citation.setVersion(Long.valueOf(3083));
            citationStore.save(citation);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return new ResponseEntity<String>("success",HttpStatus.OK);
    }
}
