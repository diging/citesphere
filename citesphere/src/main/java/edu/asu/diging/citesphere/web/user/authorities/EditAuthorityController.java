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
        model.addAttribute("entry", entry);
        model.addAttribute("form", form);
        return "auth/authority/edit";
    }
    @RequestMapping(value="/auth/authority/{authorityId}/edit/save", method=RequestMethod.POST)
    public String edit(Model model, @PathVariable("authorityId") String authorityId, Authentication authentication, AuthorityForm form) {
        IAuthorityEntry entry = authorityService.find(authorityId);
        authorityService.edit(entry, form.getName(), form.getDescription());
        return "redirect:/auth/authority/list";
    }
}
