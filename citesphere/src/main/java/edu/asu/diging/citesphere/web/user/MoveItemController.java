package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.api.v1.model.impl.MoveItem;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.AuthorityForm;


@Controller
public class MoveItemController {
	
	@Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @Autowired
    private IUserManager userManager;
    

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move", method = RequestMethod.POST )
    public @ResponseBody String moveItemToCollection(Authentication authentication,
    		@PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody MoveItem items )
    		throws ZoteroConnectionException,GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
    	ICitation citation, citation1;
    	
    	citation1 = citationManager.getCitationFromZotero((IUser) authentication.getPrincipal(),zoteroGroupId , items.itemId);
    	System.out.print(" In Move Controller Before adding, getting citation from zotero: ");
    	for(String c :citation1.getCollections()) {
    		System.out.print(c+" ");
    	}
    	System.out.println();
    	
    	try {
    		citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, items.itemId);
    	}catch(CannotFindCitationException e) {
    		return "Citation Not Found";
    	}
    	System.out.print(" In Move Controller Before adding: ");
    	for(String c :citation.getCollections()) {
    		System.out.print(c+" ");
    	}
    	System.out.println();
    	
    	// updating citation
    	citationHelper.updateCitation(citation, items.collectionId, (IUser) authentication.getPrincipal());
    	List<String> collections = citation.getCollections();
    	System.out.print(" In Move Controller After adding: ");
    	for(String c :collections) {
    		System.out.print(c+" ");
    	}
    	System.out.println();
    	
    	// citation is will be updated to zotero
    	try {
    		citationManager.updateCitation((IUser) authentication.getPrincipal(), zoteroGroupId, citation);
    	}catch(Exception e) {
    		return "Citation outdated";
    	}
    	
    	citation = citationManager.getCitation((IUser) authentication.getPrincipal(),zoteroGroupId , items.itemId);
    	System.out.print(" In Move Controller After adding, getting citation: ");
    	for(String c :citation.getCollections()) {
    		System.out.print(c+" ");
    	}
    	System.out.println();
    	
    	citation = citationManager.getCitationFromZotero((IUser) authentication.getPrincipal(),zoteroGroupId , items.itemId);
    	System.out.print(" In Move Controller After adding, getting citation from zotero: ");
    	for(String c :citation.getCollections()) {
    		System.out.print(c+" ");
    	}
    	System.out.println();
    	
    	System.out.println(items.collectionId + " "+ items.itemId);
    	return "Moved";
    	
    }
    
    
	

}
