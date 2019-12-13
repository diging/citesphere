package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitation;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        
        if (citation != null) {
            model.addAttribute("citation", citation);
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            model.addAttribute("fields", fields);
        }
        return "auth/group/items/item";
    }
    
}
