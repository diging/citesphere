package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class AddItemController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_creation_default_item_type}")
    private String defaultItemType;

    @Autowired
    @Qualifier("creatorsFile")
    private Properties properties;

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @Autowired
    private ICitationCollectionManager collectionManager;
    
    @Autowired
    private ICitationConceptManager conceptManager;
    
    @Autowired
    private IConceptTypeManager typeManager;
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/create")
    public String show(Model model, Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId) {
        model.addAttribute("form", new CitationForm());
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("defaultItemType", ItemType.valueOf(defaultItemType));
        model.addAttribute("creatorMap", properties.entrySet());
        model.addAttribute("citation", new Citation());
        List<String> fields = new ArrayList<>();
        citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), ItemType.JOURNAL_ARTICLE)
                .forEach(f -> fields.add(f.getFilename()));
        model.addAttribute("fields", fields);
        try {
            model.addAttribute("citationCollections", collectionManager.getAllCollections((IUser)authentication.getPrincipal(), zoteroGroupId, null, "title", 200));
            model.addAttribute("concepts", conceptManager.findAll(userManager.findByUsername(authentication.getName())));
            model.addAttribute("conceptTypes", typeManager.getAllTypes(userManager.findByUsername(authentication.getName())));

        } catch (GroupDoesNotExistException e) {
            logger.error("Could not retrieve collections.", e);
        }
        return "auth/group/editItem";
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/create", method = RequestMethod.POST)
    public String create(@ModelAttribute CitationForm form, Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException {
        ICitation citation = new Citation();
        List<String> collectionIds = new ArrayList<>();
        if (form.getCollectionId() != null && !form.getCollectionId().trim().isEmpty()) {
            collectionIds.add(form.getCollectionId());
        }
        citation.setCollections(collectionIds);
        citationHelper.updateCitation(citation, form, (IUser) authentication.getPrincipal());
        try {
            citation = citationManager.createCitation((IUser) authentication.getPrincipal(), zoteroGroupId,
                    collectionIds, citation);
        } catch (ZoteroItemCreationFailedException e) {
            model.addAttribute("form", form);
            model.addAttribute("zoteroGroupId", zoteroGroupId);
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            String msg = e.getResponse().getFailed().get("0") != null
                    ? e.getResponse().getFailed().get("0").getMessage()
                    : "Sorry, item creation failed.";
            model.addAttribute("alert_msg", msg);
            return "auth/group/editItem";
        }

        return "redirect:/auth/group/{zoteroGroupId}/items/" + citation.getKey();
    }
}
