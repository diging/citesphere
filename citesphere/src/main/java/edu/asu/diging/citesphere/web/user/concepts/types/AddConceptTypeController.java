package edu.asu.diging.citesphere.web.user.concepts.types;

import java.security.Principal;
import java.util.ListIterator;

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

import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;
import edu.asu.diging.citesphere.web.validation.ConceptTypeValidator;

@Controller
public class AddConceptTypeController {

    @Autowired
    private IConceptTypeManager conceptTypeManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private ConceptTypeValidator conceptValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(conceptValidator);
    }

    @RequestMapping(value = "/auth/concepts/types/add", method = RequestMethod.GET)
    public String show(Model model) {
        model.addAttribute("conceptTypeForm", new ConceptTypeForm());
        return "auth/concepts/types/add";
    }

    @RequestMapping(value = "/auth/concepts/types/add", method = RequestMethod.POST)
    public String post(@Validated @ModelAttribute("conceptTypeForm") ConceptTypeForm form, BindingResult result,
            Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            
            for(ObjectError error : result.getAllErrors()) { 
                redirectAttributes.addFlashAttribute("show_alert", true);
                redirectAttributes.addFlashAttribute("alert_msg", error.getDefaultMessage());
                redirectAttributes.addFlashAttribute("alert_type", "danger");
            }
            
            model.addAttribute("conceptTypeForm", form);
            return "redirect:/auth/concepts/types/add";
        }
        IUser user = userManager.findByUsername(principal.getName());
        conceptTypeManager.create(form, user);
        return "redirect:/auth/concepts/types/list";
    }
}
