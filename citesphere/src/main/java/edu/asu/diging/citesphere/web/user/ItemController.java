package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(required = true, value = "index") int index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        
        if (citation != null) {
            model.addAttribute("citation", citation);
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            model.addAttribute("fields", fields);
            Map<String,String> results = citationManager.getPrevAndNextCitation((IUser)authentication.getPrincipal(), zoteroGroupId, collectionId, page, sortBy, index);
            if(results.containsKey("next")) {
                model.addAttribute("next", results.get("next"));
                model.addAttribute("nextIndex", results.get("nextIndex"));
            }
            if(results.containsKey("previous")) {
                model.addAttribute("previous", results.get("previous"));
                model.addAttribute("prevIndex", results.get("prevIndex"));
            }
            model.addAttribute("page", page);
            model.addAttribute("collectionId", collectionId);
            model.addAttribute("sortBy", sortBy);
        }
        return "auth/group/items/item";
    }
    
}
