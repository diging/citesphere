package edu.asu.diging.citesphere.web.user.concepts.types;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.asu.diging.citesphere.core.model.IUser;
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

        IUser user = userManager.findByUsername(principal.getName());
        if (form.getName() != null && !form.getName().trim().isEmpty() && 
                form.getUri() != null && !form.getUri().trim().isEmpty() && 
                    conceptTypeManager.getByUriAndOwner(form.getUri(), user) == null) {
                conceptTypeManager.create(form, user);
        } else if(form.getUri() != null && !form.getUri().trim().isEmpty() && 
                conceptTypeManager.getByUriAndOwner(form.getUri(), user) != null){
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_msg", "A concept type with this URI exists.");
            model.addAttribute("alert_type", "danger");
            model.addAttribute("conceptTypeForm", form);
            return "auth/concepts/types/add";
        } else {
            model.addAttribute("conceptTypeForm", form);
            return "auth/concepts/types/add";
        }
        
        return "redirect:/auth/concepts/types/list";
    }
}
