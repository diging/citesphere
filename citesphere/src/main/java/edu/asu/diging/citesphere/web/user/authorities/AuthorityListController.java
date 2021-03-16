package edu.asu.diging.citesphere.web.user.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AuthorityListController {
    
    @Autowired
    private IAuthorityService authorityService;

    @RequestMapping("/auth/authority/list")
    public String list(Model model, Authentication authentication) {
        model.addAttribute("authorities", authorityService.getAll((IUser)authentication.getPrincipal()));
        return "auth/authorities/list";
    }
    
    @RequestMapping("/auth/authority/list/group")
    public String getAllAuthoritiesForGroup(Model model, Authentication authentication, 
            @RequestParam(defaultValue = "", required = false, value = "zoteroGroupId") String zoteroGroupId) {
        model.addAttribute("authorities", authorityService.getAll((IUser)authentication.getPrincipal()));
        return "auth/authorities/list";
    }
}
