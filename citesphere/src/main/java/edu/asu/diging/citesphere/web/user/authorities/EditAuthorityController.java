package edu.asu.diging.citesphere.web.user.authorities;

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

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.AuthorityForm;

@Controller
public class EditAuthorityController {
    
    @Autowired
    private IAuthorityService authorityService;

    @RequestMapping("/auth/authority/{authorityId}/edit")
    public String showPage(Model model, @PathVariable("authorityId") String authorityId, Authentication authentication, AuthorityForm form) {
        IAuthorityEntry entry = authorityService.find(authorityId);
        form.setName(entry.getName());
        form.setDescription(entry.getDescription());
        model.addAttribute("form", form);
        return "auth/authority/edit";
    }
    @RequestMapping(value="/auth/authority/{authorityId}/edit", method=RequestMethod.POST)
    public String edit(Model model, @PathVariable("authorityId") String authorityId, Authentication authentication, @Valid @ModelAttribute("form") AuthorityForm form, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "auth/authority/edit";
        }
        IAuthorityEntry entry = authorityService.find(authorityId);
        IUser user = (IUser) authentication.getPrincipal();
        if (!entry.getUsername().equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", "Only the owner can edit a managed authority.");
            redirectAttributes.addFlashAttribute("alert_type", "danger");
        } else {
            entry.setName(form.getName());
            entry.setDescription(form.getDescription());
            authorityService.save(entry);            
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", "Managed authority was successfully saved.");
            redirectAttributes.addFlashAttribute("alert_type", "success");
        }
        return "redirect:/auth/authority/list";
    }
}
