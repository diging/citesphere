package edu.asu.diging.citesphere.web.user.concepts;

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

import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

@Controller
public class EditConceptController {
    @Autowired
    private ICitationConceptManager conceptManager;
    
    @Autowired
    private IConceptTypeManager typeManager;
    
    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value="/auth/concepts/{conceptId}/edit", method=RequestMethod.GET)
    public String show(Model model, @PathVariable("conceptId") String conceptId, Authentication authentication, CitationConceptForm form) {
        ICitationConcept citationConcept = conceptManager.get(conceptId);
        form.setName(citationConcept.getName());
        form.setDescription(citationConcept.getDescription());
        form.setUri(citationConcept.getUri());
        return "auth/concepts/edit";
    }
    
    @RequestMapping(value="/auth/concepts/{conceptId}/edit", method=RequestMethod.POST)
    public String post(Model model, @PathVariable("conceptId") String conceptId, Authentication authentication, @Valid @ModelAttribute("form") CitationConceptForm form, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("form", form);
        }
        return "";
    }

}
