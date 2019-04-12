package edu.asu.diging.citesphere.web.user.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.core.service.IAuthorityService;

@Controller
public class AuthorityController {

    @Autowired
    private IAuthorityService authorityService;

    @RequestMapping("/auth/authority/{authorityId}")
    public String show(@PathVariable("authorityId") String authorityId, Model model) {
        IAuthorityEntry entry = authorityService.find(authorityId);
        model.addAttribute("entry", entry);
        return "auth/authority";
    }
}
