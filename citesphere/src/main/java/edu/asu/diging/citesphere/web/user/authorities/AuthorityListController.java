package edu.asu.diging.citesphere.web.user.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AuthorityListController {
    
    @Autowired
    private IAuthorityService authorityService;
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping("/auth/authority/list")
    public String list(Model model, Authentication authentication) {
        model.addAttribute("authorities", authorityService.getAll((IUser)authentication.getPrincipal()));   
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", "all");
        return "auth/authorities/list";
    }
    
    @RequestMapping("/auth/authority/list/{zoteroGroupId}")
    public String getAuthoritiesForGroup(Model model, Authentication authentication, 
            @PathVariable("zoteroGroupId") String zoteroGroupId) {
        model.addAttribute("authorities", authorityService.getAuthoritiesByGroup(Long.valueOf(zoteroGroupId)));
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", zoteroGroupId);
        return "auth/authorities/list";
    }
}
