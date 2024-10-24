package edu.asu.diging.citesphere.web.user.authorities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CreateAuthorityForm;

@Controller
public class CreateAuthorityController {

    @Autowired
    private IAuthorityService authorityService;

    @Autowired
    private ICitationManager citationManager;

    @GetMapping(value = { "/auth/authority/create", "/auth/authority/{zoteroGroupId}/create" })
    public String show(Authentication authentication, Model model,
            @PathVariable(required = false, value = "zoteroGroupId") String zoteroGroupId) {
        IUser user = (IUser) authentication.getPrincipal();
        model.addAttribute("groups", citationManager.getGroups(user));
        CreateAuthorityForm authorityForm = new CreateAuthorityForm();
        authorityForm.setGroupId(zoteroGroupId);
        model.addAttribute("authorityForm", authorityForm);
        return "/auth/authorities/create";
    }

    @PostMapping(value = "/auth/authority/create")
    public String add(@Validated @ModelAttribute("authorityForm") CreateAuthorityForm form, BindingResult result,
            Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        IUser user = (IUser) authentication.getPrincipal();
        if (result.hasErrors()) {
            model.addAttribute("groups", citationManager.getGroups(user));
            model.addAttribute("authorityForm", form);
            return "auth/authorities/create";
        }
        IAuthorityEntry entry = new AuthorityEntry();
        entry.setName(form.getName());
        entry.setDescription(form.getDescription());
        if (form.getImporterId() != null && !form.getImporterId().isEmpty()) {
            entry.setImporterId(form.getImporterId());
        }
        Set<Long> groups = new HashSet<>();
        if (form.getGroupId() != null && !form.getGroupId().isEmpty()) {
            groups.add(Long.valueOf(form.getGroupId()));
        }
        entry.setGroups(groups);
        if (form.getUri() != null && !form.getUri().isEmpty()) {
            entry.setUri(form.getUri());
            entry = authorityService.create(entry, user);
        } else {
            entry = authorityService.createWithUri(entry, user);
        }
        redirectAttributes.addFlashAttribute("show_alert", true);
        redirectAttributes.addFlashAttribute("alert_msg", "Managed authority was successfully created.");
        redirectAttributes.addFlashAttribute("alert_type", "success");
        if (form.getGroupId() != null && !form.getGroupId().isEmpty()) {
            return "redirect:/auth/authority/" + form.getGroupId() + "/list";
        }
        return "redirect:/auth/authority/list";
    }

}
