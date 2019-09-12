package edu.asu.diging.citesphere.web.user.concepts;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

@Controller
public class AddConceptController {

    @Autowired
    private ICitationConceptManager conceptManager;
    
    @Autowired
    private IConceptTypeManager typeManager;
    
    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value="/auth/concepts/add", method=RequestMethod.GET)
    public String show(Model model, Principal principal) {
        model.addAttribute("conceptForm", new CitationConceptForm());
        model.addAttribute("types", typeManager.getAllTypes(userManager.findByUsername(principal.getName())));
        return "auth/concepts/add";
    }
    
    @RequestMapping(value="/auth/concepts/add", method=RequestMethod.POST)
    public String post(CitationConceptForm form, Model model, Principal principal) {
        
        IUser user = userManager.findByUsername(principal.getName());
        if (form.getName() != null && !form.getName().trim().isEmpty() && 
                form.getUri() != null && !form.getUri().trim().isEmpty() && 
                        conceptManager.getByUriAndOwner(form.getUri(), user) == null) {
            conceptManager.create(form, user);
        } else if(form.getUri() != null && !form.getUri().trim().isEmpty() && 
                conceptManager.getByUriAndOwner(form.getUri(), user) != null){
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_msg", "A concept with this URI exists.");
            model.addAttribute("alert_type", "danger");
            model.addAttribute("conceptForm", form);
            return "auth/concepts/add";
        } else {
            model.addAttribute("conceptForm", form);
            return "auth/concepts/add";
        }
        
        return "redirect:/auth/concepts/list";
    }
}
