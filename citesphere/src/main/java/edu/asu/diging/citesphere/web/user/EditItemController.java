package edu.asu.diging.citesphere.web.user;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.IAffiliation;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.core.model.bib.impl.Person;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.web.forms.AffiliationForm;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.web.forms.PersonForm;

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
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId)
            throws GroupDoesNotExistException {
        ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("citation", citation);
        model.addAttribute("form", form);
        if (model.containsAttribute("resolvedForm")) {
            CitationForm resolvedCitationForm = (CitationForm) model.asMap().get("resolvedForm");

            Set<IPerson> resolvedAuthorSet = personFormListToPersonSet(resolvedCitationForm.getAuthors());
            model.addAttribute("resolvedAuthors", resolvedAuthorSet);

            Set<IPerson> resolvedEditorSet = personFormListToPersonSet(resolvedCitationForm.getEditors());
            model.addAttribute("resolvedEditors", resolvedEditorSet);

            model.addAttribute("form", resolvedCitationForm);
        }
        List<String> fields = new ArrayList<>();
        citationManager.getItemTypeFields((IUser) authentication.getPrincipal(), citation.getItemType())
                .forEach(f -> fields.add(f.getFilename()));
        model.addAttribute("fields", fields);
        model.addAttribute("creatorMap", properties.entrySet());

        model.addAttribute("concepts", conceptManager.findAll(userManager.findByUsername(authentication.getName())));
        model.addAttribute("conceptTypes",
                typeManager.getAllTypes(userManager.findByUsername(authentication.getName())));

        return "auth/group/items/item/edit";
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
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId)
            throws ZoteroConnectionException, GroupDoesNotExistException {
        ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
        if (form.getVersion() == citation.getVersion()) {

            // load authors and editors before detaching
            citation.getAuthors().forEach(a -> a.getAffiliations().size());
            System.out.println(citation.getAuthors().size());
            citation.getEditors().forEach(e -> e.getAffiliations().size());
            citationManager.detachCitation(citation);
            citationHelper.updateCitation(citation, form);

            try {
                citationManager.updateCitation((IUser) authentication.getPrincipal(), zoteroGroupId, citation);
            } catch (CitationIsOutdatedException e) {
                System.out.println(e);
            }

        } else {
            citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, itemId);
            ICitation currentCitation = citationManager.getCitationFromZotero((IUser) authentication.getPrincipal(),
                    zoteroGroupId, itemId);
            model.addAttribute("outdatedCitation", citation);
            model.addAttribute("currentCitation", currentCitation);
            model.addAttribute("form", form);
            model.addAttribute("zoteroGroupId", zoteroGroupId);

            Set<IPerson> currentAuthors = currentCitation.getAuthors();
            Set<IPerson> formAuthors = personFormListToPersonSet(form.getAuthors());
            if (!currentAuthors.equals(formAuthors)) {
                model.addAttribute("authorsChanged", true);
            } else {
                model.addAttribute("authorsChanged", false);
            }

            Set<IPerson> currentEditors = currentCitation.getEditors();
            Set<IPerson> formEditors = personFormListToPersonSet(form.getEditors());
            if (!currentEditors.equals(formEditors)) {
                model.addAttribute("editorsChanged", true);
            } else {
                model.addAttribute("editorsChanged", false);
            }

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

            return "auth/group/items/item/edit/conflict";
        }
        return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}";
    }

    private Set<IPerson> personFormListToPersonSet(List<PersonForm> personForms) {
        Set<IPerson> set = new HashSet<>();

        if (personForms != null) {
            for (PersonForm personForm : personForms) {
                Person person = new Person();
                person.setId(personForm.getId());
                person.setFirstName(personForm.getFirstName());
                person.setLastName(personForm.getLastName());
                person.setName(String.join(" ", personForm.getFirstName(), personForm.getLastName()));
                person.setLocalAuthorityId(personForm.getLocalAuthorityId());
                person.setPositionInList(personForm.getPosition());
                person.setUri(personForm.getUri());
                person.setAffiliations(new HashSet<>());
                if (personForm.getAffiliations() != null) {
                    for (AffiliationForm affiliationForm : personForm.getAffiliations()) {
                        IAffiliation affiliation = new Affiliation();
                        affiliation.setId(affiliationForm.getId());
                        affiliation.setName(affiliationForm.getName());
                        person.getAffiliations().add(affiliation);
                    }
                }

                set.add(person);
            }
        }

        return set;
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/conflict/resolve", method = RequestMethod.POST)
    public String resolveConflict(@ModelAttribute CitationForm form, Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            RedirectAttributes redirectAttrs) throws GroupDoesNotExistException {

        ICitation citation = citationManager.updateCitationFromZotero((IUser) authentication.getPrincipal(),
                zoteroGroupId, itemId);
        List<String> changedFields = compare(citation, form);
        updateForm(citation, form, changedFields);

        List<PersonForm> authorForms = form.getAuthors();
        for (PersonForm personForm : Optional.ofNullable(authorForms).orElse(Collections.emptyList())) {
            List<AffiliationForm> affiliationForms = personForm.getAffiliations();
            for (AffiliationForm affiliationForm : Optional.ofNullable(affiliationForms)
                    .orElse(Collections.emptyList())) {
                affiliationForm.setId(null);
            }
            personForm.setId(null);
        }

        List<PersonForm> editorForms = form.getEditors();
        for (PersonForm personForm : Optional.ofNullable(editorForms).orElse(Collections.emptyList())) {
            List<AffiliationForm> affiliationForms = personForm.getAffiliations();
            for (AffiliationForm affiliationForm : Optional.ofNullable(affiliationForms)
                    .orElse(Collections.emptyList())) {
                affiliationForm.setId(null);
            }
            personForm.setId(null);
        }

        redirectAttrs.addFlashAttribute("resolvedForm", form);
        return "redirect:/auth/group/{zoteroGroupId}/items/{itemId}/edit";
    }

    private List<String> compare(ICitation citation, CitationForm form) {
        List<String> changedFields = new ArrayList<>();
        Field[] fields = form.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldname = field.getName();
            try {
                field.setAccessible(true);

                if (fieldname.equals("authors") || fieldname.equals("editors")) {

                    List<PersonForm> formValue = field.get(form) != null ? (List<PersonForm>) field.get(form) : null;
                    Set<IPerson> formValueSet = formValue != null ? personFormListToPersonSet(formValue) : null;
                    Field citationField = citation.getClass().getDeclaredField(fieldname);
                    citationField.setAccessible(true);
                    TreeSet<IPerson> citationValue = citationField.get(citation) != null
                            ? (TreeSet<IPerson>) citationField.get(citation)
                            : null;
                    Set<IPerson> citationValueSet = citationValue != null ? new HashSet<>(citationValue) : null;

                    if (!formValueSet.equals(citationValueSet)) {
                        changedFields.add(fieldname);
                    }

                } else {

                    String formValue = field.get(form) != null ? field.get(form).toString() : "";
                    Field citationField = citation.getClass().getDeclaredField(fieldname);
                    citationField.setAccessible(true);
                    String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString()
                            : "";

                    if (!formValue.equals(citationValue)) {
                        changedFields.add(fieldname);
                    }
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

                if (fieldName.equals("authors") || fieldName.equals("editors")) {
                    try {
                        Field citationField = citation.getClass().getDeclaredField(fieldName);
                        citationField.setAccessible(true);
                        TreeSet<IPerson> citationValue = citationField.get(citation) != null
                                ? (TreeSet<IPerson>) citationField.get(citation)
                                : null;
                        field.setAccessible(true);
                        field.set(form, citationValue);
                    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                            | IllegalAccessException e) {

                        logger.error("Could ont access field.", e);
                        // let's ignore that for nonw
                    }
                } else {

                    try {
                        Field citationField = citation.getClass().getDeclaredField(fieldName);
                        citationField.setAccessible(true);
                        String citationValue = citationField.get(citation) != null
                                ? citationField.get(citation).toString()
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
}