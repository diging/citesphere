package edu.asu.diging.citesphere.web.user.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
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
        model.addAttribute("entry_name", entry.getName());
        return "auth/authority/edit";
    }
    @RequestMapping(value="/auth/authority/{authorityId}/edit", method=RequestMethod.POST)
    public String edit(Model model, @PathVariable("authorityId") String authorityId, Authentication authentication, AuthorityForm form, RedirectAttributes redirectAttributes) {
        IAuthorityEntry entry = authorityService.find(authorityId);
        IUser user = (IUser) authentication.getPrincipal();
        if (!entry.getUsername().equals(user.getUsername())) {
            String msg="Only Owner can edit managed authorities";
            redirectAttributes.addFlashAttribute("show_alert", true);
            redirectAttributes.addFlashAttribute("alert_msg", msg);
            redirectAttributes.addFlashAttribute("alert_type", "danger");
        }
        else {
            entry.setName(form.getName());
            entry.setDescription(form.getDescription());
            authorityService.saveEntry(entry, user);
        }
        return "redirect:/auth/authority/list";
    }
}
