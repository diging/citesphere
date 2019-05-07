package edu.asu.diging.citesphere.web.user.concepts;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    
    @RequestMapping(value="/auth/concepts/edit", method=RequestMethod.GET)
    public String show(Model model, Principal principal) {
        model.addAttribute("conceptForm", new CitationConceptForm());
        model.addAttribute("types", typeManager.getAllTypes(userManager.findByUsername(principal.getName())));
        return "auth/concepts/add";
    }
    
    @RequestMapping(value="/auth/concepts/edit", method=RequestMethod.POST)
    public String post(CitationConceptForm form, Model model, Principal principal) {
        
        if (form.getUri() != null && !form.getUri().trim().isEmpty() && form.getName() != null && !form.getName().trim().isEmpty()) {
            conceptManager.create(form, userManager.findByUsername(principal.getName()));
        } else {
            model.addAttribute("conceptForm", form);
            return "auth/concepts/add";
        }
        
        return "redirect:/auth/concepts/list";
    }

}
