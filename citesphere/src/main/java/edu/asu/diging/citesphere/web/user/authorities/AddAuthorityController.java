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
public class AddAuthorityController {

    @Value("${_authority_uri}")
    private String authorityUri;

    @Value("${_authority_prefix}")
    private String authorityPrefix;

    @Autowired
    private IAuthorityService authorityService;

    @GetMapping(value = "/auth/authority/new")
    public String show(Model model) {
        model.addAttribute("authorityForm", new AuthorityForm());
        return "auth/authorities/new";
    }

    @PostMapping(value = "/auth/authority/new")
    public String add(@Validated @ModelAttribute("authorityForm") AuthorityForm form, BindingResult result, Model model,
            Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("authorityForm", form);
            return "auth/authorities/new";
        }
        IAuthorityEntry entry = new AuthorityEntry();
        entry.setName(form.getName());
        entry.setDescription(form.getDescription());
        entry.setGroups(new HashSet<>());
        entry = authorityService.create(entry, (IUser) authentication.getPrincipal(), authorityUri + authorityPrefix);
        return "redirect:/auth/authority/list";
    }

}
