package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

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

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemDeletionFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class DeleteItemController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}", method = RequestMethod.DELETE)
    public String deleteItem(@ModelAttribute CitationForm form, Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId)
            throws ZoteroConnectionException, GroupDoesNotExistException, CannotFindCitationException,
            ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
        List<String> collectionIds = new ArrayList<>();
        if (form.getCollectionId() != null && !form.getCollectionId().trim().isEmpty()) {
            collectionIds.add(form.getCollectionId());
        }
        try {
            citationManager.deleteCitation((IUser) authentication.getPrincipal(), zoteroGroupId, collectionIds,
                    citation);
        } catch (ZoteroItemDeletionFailedException e) {
            
            model.addAttribute("form", form);
            model.addAttribute("zoteroGroupId", zoteroGroupId);
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            String msg = e.getResponse().getFailed().get("0") != null
                    ? e.getResponse().getFailed().get("0").getMessage()
                    : "Sorry, item deletion failed.";
            model.addAttribute("alert_msg", msg);
            return "/auth/group/"+ zoteroGroupId + "/items/" + itemId;
         
        }

        return "redirect:/auth/group/{zoteroGroupId}/items";
    }

}