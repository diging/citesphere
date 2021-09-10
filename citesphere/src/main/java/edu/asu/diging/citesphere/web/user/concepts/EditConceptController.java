package edu.asu.diging.citesphere.web.user.concepts;

import java.util.ListIterator;

import javax.validation.Valid;

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

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;
import edu.asu.diging.citesphere.web.validation.CitationConceptValidator;

@Controller
public class EditConceptController {

    @Autowired
    private ICitationConceptManager conceptManager;

    @Autowired
    private CitationConceptValidator conceptValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(conceptValidator);
    }

    @RequestMapping(value = "/auth/concepts/{conceptId}/edit")
    public String show(Model model, @PathVariable("conceptId") String conceptId, Authentication authentication,
            CitationConceptForm form) {
        ICitationConcept citationConcept = conceptManager.get(conceptId);
        form.setName(citationConcept.getName());
        form.setDescription(citationConcept.getDescription());
        form.setUri(citationConcept.getUri());
        form.setOwner(citationConcept.getOwner().getFirstName() + " " + citationConcept.getOwner().getLastName());
        model.addAttribute("form", form);
        return "auth/concepts/edit";
    }

    @RequestMapping(value = "/auth/concepts/{conceptId}/edit", method = RequestMethod.POST)
    public String post(Model model, @PathVariable("conceptId") String conceptId, Authentication authentication,
            @Valid @ModelAttribute("form") CitationConceptForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
    	if (result.hasErrors()) {
        	ListIterator<ObjectError> listIterator = result.getAllErrors().listIterator();
        	
        	while(listIterator.hasNext())
        	{
        		ObjectError error = listIterator.next();
        		
        		if(error.getDefaultMessage().equals("must be a valid URL"))
        		{
        			redirectAttributes.addFlashAttribute("show_alert", true);
        			redirectAttributes.addFlashAttribute("alert_msg", "URI must be a valid one");
        			redirectAttributes.addFlashAttribute("alert_type", "danger");
        		
        			return "redirect:/auth/concepts/list";
        		}
        	}
        	
            model.addAttribute("form", form);
            return "auth/concepts/edit";
        }
        ICitationConcept citationConcept = conceptManager.get(conceptId);
        IUser user = (IUser) authentication.getPrincipal();

        if (!citationConcept.getOwner().getUsername().equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", "Only the owner can edit a Concept.");
            redirectAttributes.addFlashAttribute("alert_type", "danger");
        } else {
            citationConcept.setName(form.getName());
            citationConcept.setDescription(form.getDescription());
            citationConcept.setUri(form.getUri());

            conceptManager.save(citationConcept);
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", "Concept was successfully saved.");
            redirectAttributes.addFlashAttribute("alert_type", "success");
        }
        return "redirect:/auth/concepts/list";
    }

}
