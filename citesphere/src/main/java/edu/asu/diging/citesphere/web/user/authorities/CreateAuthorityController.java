package edu.asu.diging.citesphere.web.user.authorities;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.AuthorityForm;

@Controller
public class CreateAuthorityController {

    @Autowired
    private IAuthorityService authorityService;

    @GetMapping(value = "/auth/authority/create")
    public String show(Model model) {
        model.addAttribute("authorityForm", new AuthorityForm());
        return "/auth/authorities/create";
    }

    @PostMapping(value = "/auth/authority/create")
    public String add(@Validated @ModelAttribute("authorityForm") AuthorityForm form, BindingResult result, Model model,
            Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("authorityForm", form);
            return "auth/authorities/create";
        }
        IAuthorityEntry entry = new AuthorityEntry();
        entry.setName(form.getName());
        entry.setDescription(form.getDescription());
        entry.setGroups(new HashSet<>());
        entry = authorityService.createWithUri(entry, (IUser) authentication.getPrincipal());
        return "redirect:/auth/authority/list";
    }

}
