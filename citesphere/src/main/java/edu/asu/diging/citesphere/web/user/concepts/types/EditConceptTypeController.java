package edu.asu.diging.citesphere.web.user.concepts.types;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;
import edu.asu.diging.citesphere.web.validation.ConceptTypeValidator;

@Controller
public class EditConceptTypeController {

    @Autowired
    private IConceptTypeManager conceptTypeManager;

    @Autowired
    private ConceptTypeValidator conceptValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(conceptValidator);
    }

    @RequestMapping(value = "/auth/concepts/types/{conceptTypeId}/edit")
    public String show(Model model, @PathVariable("conceptTypeId") String typeId, Authentication authentication,
            ConceptTypeForm form) {
        IConceptType conceptType = conceptTypeManager.get(typeId);
        form.setName(conceptType.getName());
        form.setDescription(conceptType.getDescription());
        form.setUri(conceptType.getUri());
        form.setOwner(conceptType.getOwner().getFirstName() + " " + conceptType.getOwner().getLastName());
        model.addAttribute("form", form);
        return "auth/concepts/types/edit";
    }

    @RequestMapping(value = "/auth/concepts/types/{conceptTypeId}/edit", method = RequestMethod.POST)
    public String post(Model model, @PathVariable("conceptTypeId") String typeId, Authentication authentication,
            @Validated @ModelAttribute("form") ConceptTypeForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            
            for(ObjectError error : result.getAllErrors()) {
                redirectAttributes.addFlashAttribute("show_alert", true);
                redirectAttributes.addFlashAttribute("alert_msg", error.getDefaultMessage());
                redirectAttributes.addFlashAttribute("alert_type", "danger");
                      
            }
            
            model.addAttribute("form", form);
            return "redirect:/auth/concepts/types/{conceptTypeId}/edit";
        }
        IConceptType conceptType = conceptTypeManager.get(typeId);
        conceptType.setName(form.getName());
        conceptType.setDescription(form.getDescription());
        conceptType.setUri(form.getUri());

        conceptTypeManager.save(conceptType);
        redirectAttributes.addFlashAttribute("show_alert", true);
        redirectAttributes.addFlashAttribute("alert_msg", "Concept Type was successfully saved.");
        redirectAttributes.addFlashAttribute("alert_type", "success");

        return "redirect:/auth/concepts/types/list";
    }

}
