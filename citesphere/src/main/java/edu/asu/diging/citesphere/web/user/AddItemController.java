package edu.asu.diging.citesphere.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class AddItemController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/create")
    public String show(Model model, @PathVariable("zoteroGroupId") String zoteroGroupId) {
        model.addAttribute("form", new CitationForm());
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        return "auth/group/items/create";
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/create", method = RequestMethod.POST)
    public String create(@ModelAttribute CitationForm form, Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId) throws ZoteroConnectionException, ZoteroItemCreationFailedException {
        ICitation citation = new Citation();
        citationHelper.updateCitation(citation, form);
        citation = citationManager.createCitation((IUser)authentication.getPrincipal(), zoteroGroupId, citation);
        
        return "redirect:/auth/group/{zoteroGroupId}/items/" + citation.getKey();
    }
}
