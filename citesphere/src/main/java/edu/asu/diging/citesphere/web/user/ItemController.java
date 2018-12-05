package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Controller
public class ItemController {

    @Autowired
    private IZoteroManager zoteroManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) {
        ICitation citation = zoteroManager.getGroupItem((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("citation", citation);
        return "auth/group/items/item";
    }
}
