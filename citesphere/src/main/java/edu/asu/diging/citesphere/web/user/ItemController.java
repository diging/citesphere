package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.service.ICitationManager;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("citation", citation);
        List<String> fields = new ArrayList<>();
        citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
        model.addAttribute("fields", fields);
        System.out.println(citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()));
        System.out.println(fields.size());
        return "auth/group/items/item";
    }
}
