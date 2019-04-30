package edu.asu.diging.citesphere.web.user.concepts.types;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

@Controller
public class AddConceptTypeController {

    @Autowired
    private IConceptTypeManager conceptTypeManager;
    
    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value="/auth/concepts/types/add", method=RequestMethod.GET)
    public String show(Model model) {
        model.addAttribute("conceptTypeForm", new ConceptTypeForm());
        return "auth/concepts/types/add";
    }
    
    @RequestMapping(value="/auth/concepts/types/add", method=RequestMethod.POST)
    public String post(ConceptTypeForm form, Model model, Principal principal) {
        
        if (form.getName() != null && !form.getName().trim().isEmpty()) {
            conceptTypeManager.create(form, userManager.findByUsername(principal.getName()));
        } else {
            model.addAttribute("conceptTypeForm", form);
            return "auth/concepts/types/add";
        }
        
        return "redirect:/auth/concepts/types/list";
    }
}
