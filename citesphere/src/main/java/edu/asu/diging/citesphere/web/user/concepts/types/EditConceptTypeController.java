package edu.asu.diging.citesphere.web.user.concepts.types;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.model.bib.IConceptType;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

@Controller
public class EditConceptTypeController {
    @Autowired
    private IConceptTypeManager conceptTypeManager;

    @RequestMapping(value="/auth/concepts/types/{typeId}/edit")
    public String show(Model model, @PathVariable("typeId") String typeId, Authentication authentication, ConceptTypeForm form) {
        IConceptType conceptType = conceptTypeManager.get(typeId);
        form.setName(conceptType.getName());
        form.setDescription(conceptType.getDescription());
        form.setUri(conceptType.getUri());
        model.addAttribute("form", form);
        return "auth/concepts/types/edit";
    }

    @RequestMapping(value="/auth/concepts/types/{typeId}/edit", method=RequestMethod.POST)
    public String post(Model model, @PathVariable("typeId") String typeId, Authentication authentication, @Valid @ModelAttribute("form") ConceptTypeForm form, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "auth/concepts/types/edit";
        }
        IConceptType conceptType = conceptTypeManager.get(typeId);
        IUser user = (IUser) authentication.getPrincipal();

        if (!conceptType.getOwner().getUsername().equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", "Only the owner can edit a Concept Type.");
            redirectAttributes.addFlashAttribute("alert_type", "danger");
        } else {
            conceptType.setName(form.getName());
            conceptType.setDescription(form.getDescription());
            conceptType.setUri(form.getUri());

            conceptTypeManager.save(conceptType);            
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", "Concept Type was successfully saved.");
            redirectAttributes.addFlashAttribute("alert_type", "success");
        }
        return "redirect:/auth/concepts/types/list";
    }

}