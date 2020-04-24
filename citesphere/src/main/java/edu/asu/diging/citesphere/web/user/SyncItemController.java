package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class SyncItemController {
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/sync")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId, @RequestParam(required = false, value = "index") String index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        citationManager.updateCitationFromZotero((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}?index=" + index +"&page="+page +"&sortBy="+sortBy +"&collectionId="+collectionId;
    }
}