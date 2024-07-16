package edu.asu.diging.citesphere.web.user;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class EditItemController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("creatorsFile")
    private Properties properties;

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @Autowired
    private ICitationConceptManager conceptManager;

    @Autowired
    private IConceptTypeManager typeManager;

    @Autowired
    private IUserManager userManager;

    @RequestMapping("/auth/group/{zoteroGroupId}/items/{itemId}/edit")
    public String showPage(Authentication authentication, Model model, CitationForm form,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId, @RequestParam(required = false, value = "index") String index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy)
            throws GroupDoesNotExistException, ZoteroHttpStatusException, DuplicateKeyException {
        ICitation citation;
        try {
            citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
        } catch (CannotFindCitationException e) {
            return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}?index=" + index +"&page="+page +"&sortBy="+sortBy +"&collectionId="+collectionId;
        } catch (DuplicateKeyException e) {
//            return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}?index=" + index +"&page="+page +"&sortBy="+sortBy +"&collectionId="+collectionId;
            return "redirect:/auth/group/{zoteroGroupId}/items";
        }
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("citation", citation);
        if (model.containsAttribute("resolvedForm")) {
            model.addAttribute("form", model.asMap().get("resolvedForm"));
        } else {
            model.addAttribute("form", form);
        }
        List<String> fields = new ArrayList<>();
        citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), citation.getItemType())
                .forEach(f -> fields.add(f.getFilename()));
        model.addAttribute("fields", fields);
        Set<Entry<Object, Object>> labels = properties.entrySet();
        Map<String, String> labelsRoles = new HashMap<>();
        labels.forEach(x -> labelsRoles.put(x.getKey().toString(), x.getValue().toString()));
        for (String role : citation.getOtherCreatorRoles()) {
            if (!labelsRoles.containsKey("_item_attribute_label_" + role)) {
                labelsRoles.put("_item_attribute_label_" + role, org.springframework.util.StringUtils.capitalize(role));
            }
        }
        model.addAttribute("creatorMap", labelsRoles.entrySet());

        model.addAttribute("concepts", conceptManager.findAll(userManager.findByUsername(authentication.getName())));
        model.addAttribute("conceptTypes",
                typeManager.getAllTypes(userManager.findByUsername(authentication.getName())));
        model.addAttribute("index", index);
        model.addAttribute("page", page);
        model.addAttribute("collectionId", collectionId);
        model.addAttribute("sortBy", sortBy);
        return "auth/group/editItem";
    }

    /**
     * Method to retrieve all fields filtered by item type.
     */
    @RequestMapping("/auth/items/{itemType}/fields")
    public ResponseEntity<List<String>> getFieldsByItemType(Authentication authentication,
            @PathVariable("itemType") ItemType itemType) {
        List<String> fields = new ArrayList<>();
        citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), itemType)
                .forEach(f -> fields.add(f.getFilename()));
        return new ResponseEntity<List<String>>(fields, HttpStatus.OK);
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/edit", method = RequestMethod.POST)
    public String storeItem(@ModelAttribute CitationForm form, Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(required = false, value = "index") String index,
            @RequestParam(defaultValue = "1", required = false, value = "page") int page,
            @RequestParam(value = "collectionId", required = false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy)
            throws ZoteroConnectionException, GroupDoesNotExistException, CannotFindCitationException,
            ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
        // load authors and editors before detaching
        citationHelper.updateCitation(citation, form, (IUser) authentication.getPrincipal());
        try {
            citationManager.updateCitation((IUser) authentication.getPrincipal(), zoteroGroupId, citation);
        } catch (CitationIsOutdatedException e) {
            citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
            ICitation currentCitation = citationManager.getCitationFromZotero((IUser) authentication.getPrincipal(),
                    zoteroGroupId, itemId);
            model.addAttribute("outdatedCitation", citation);
            model.addAttribute("currentCitation", currentCitation);
            model.addAttribute("form", form);
            model.addAttribute("zoteroGroupId", zoteroGroupId);

            List<String> outdatedCitationFields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), citation.getItemType())
                    .forEach(f -> outdatedCitationFields.add(f.getFilename()));
            model.addAttribute("outdatedCitationFields", outdatedCitationFields);

            List<String> currentCitationFields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), currentCitation.getItemType())
                    .forEach(f -> currentCitationFields.add(f.getFilename()));
            model.addAttribute("currentCitationFields", currentCitationFields);

            List<String> formFields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), form.getItemType())
                    .forEach(f -> formFields.add(f.getFilename()));
            model.addAttribute("formFields", formFields);
            model.addAttribute("index", index);
            model.addAttribute("page", page);
            model.addAttribute("collectionId", collectionId);
            model.addAttribute("sortBy", sortBy);
            return "auth/group/editConflict";
        } catch (DuplicateKeyException e) {
            return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}?index=" + index +"&page="+page +"&sortBy="+sortBy +"&collectionId="+collectionId;
        }
        return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}?index=" + index +"&page="+page +"&sortBy="+sortBy +"&collectionId="+collectionId;
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/conflict/resolve", method = RequestMethod.POST)
    public String resolveConflict(@ModelAttribute CitationForm form, Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            RedirectAttributes redirectAttrs, @RequestParam(required = false, value = "index") String index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException, DuplicateKeyException {
        ICitation outdatedCitation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId,
                itemId);
        List<String> changedFields = compare(outdatedCitation, form);
        ICitation citation = citationManager.updateCitationFromZotero((IUser) authentication.getPrincipal(),
                zoteroGroupId, itemId);
        updateForm(citation, form, changedFields);

        redirectAttrs.addFlashAttribute("resolvedForm", form);
        return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}/edit?index=" + index +"&page="+page +"&sortBy="+sortBy +"&collectionId="+collectionId;
    }

    private List<String> compare(ICitation citation, CitationForm form) {
        List<String> changedFields = new ArrayList<>();
        Field[] fields = form.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldname = field.getName();
            try {
                field.setAccessible(true);
                String formValue = field.get(form) != null ? field.get(form).toString() : "";
                Field citationField = citation.getClass().getDeclaredField(fieldname);
                citationField.setAccessible(true);
                String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString()
                        : "";
                // if values are the same, continue
                formValue = formValue != null ? formValue : "";
                citationValue = citationValue != null ? citationValue : "";

                if (!formValue.equals(citationValue)) {
                    changedFields.add(fieldname);
                }

            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                logger.error("Could ont access field.", e);
                // let's ignore that for nonw
            }
        }
        return changedFields;
    }

    private void updateForm(ICitation citation, CitationForm form, List<String> changedFields) {
        Field[] allFields = form.getClass().getDeclaredFields();
        for (Field field : allFields) {
            String fieldName = field.getName();
            if (!changedFields.contains(fieldName)) {
                try {
                    Field citationField = citation.getClass().getDeclaredField(fieldName);
                    citationField.setAccessible(true);
                    String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString()
                            : "";
                    field.setAccessible(true);
                    field.set(form, citationValue);
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                        | IllegalAccessException e) {
                    logger.error("Could ont access field.", e);
                    // let's ignore that for nonw
                }
            }
        }
    }
}