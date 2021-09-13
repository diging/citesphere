package edu.asu.diging.citesphere.web.user.concepts;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;
import edu.asu.diging.citesphere.web.validation.CitationConceptValidator;

@Controller
public class AddConceptController {

    @Autowired
    private ICitationConceptManager conceptManager;

    @Autowired
    private IConceptTypeManager typeManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private CitationConceptValidator conceptValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(conceptValidator);
    }

    @RequestMapping(value = "/auth/concepts/add", method = RequestMethod.GET)
    public String show(Model model, Principal principal) {
        model.addAttribute("conceptForm", new CitationConceptForm());
        model.addAttribute("types", typeManager.getAllTypes(userManager.findByUsername(principal.getName())));
        return "auth/concepts/add";
    }

    @RequestMapping(value = "/auth/concepts/add", method = RequestMethod.POST)
    public String post(@Validated @ModelAttribute("conceptForm") CitationConceptForm form, BindingResult result,
            Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            for(ObjectError error : result.getAllErrors()) {
                redirectAttributes.addFlashAttribute("show_alert", true);
                redirectAttributes.addFlashAttribute("alert_msg", error.getDefaultMessage());
                redirectAttributes.addFlashAttribute("alert_type", "danger");
            }
            
            model.addAttribute("conceptForm", form);
            return "redirect:/auth/concepts/add";
        }
    
        IUser user = userManager.findByUsername(principal.getName());
        conceptManager.create(form, user);
        return "redirect:/auth/concepts/list";
    }
}
