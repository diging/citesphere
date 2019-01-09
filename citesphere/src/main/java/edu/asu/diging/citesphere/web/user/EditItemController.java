package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class EditItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationHelper citationHelper;
    
    @RequestMapping("/auth/group/{zoteroGroupId}/items/{itemId}/edit")
    public String showPage(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("citation", citation);
        model.addAttribute("form", new CitationForm());
        return "auth/group/items/item/edit";
    }
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/edit", method = RequestMethod.POST)
    public String storeItem(@ModelAttribute CitationForm form, Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        citationHelper.updateCitation(citation, form);
        citationManager.updateCitation(citation);
        return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}";
    }
}
