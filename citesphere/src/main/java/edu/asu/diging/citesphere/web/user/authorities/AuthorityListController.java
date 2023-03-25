package edu.asu.diging.citesphere.web.user.authorities;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AuthorityListController {
    
    @Autowired
    private IAuthorityService authorityService;
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping("/auth/authority/list")
    public String list(Model model, Authentication authentication) {
        IUser user = (IUser)authentication.getPrincipal();
        List<ICitationGroup> userGroups = citationManager.getGroups((IUser) authentication.getPrincipal());
        List<IAuthorityEntry> allAuthorities = authorityService.getAll(user, userGroups.stream().map(group -> group.getGroupId()).collect(Collectors.toList()));
        model.addAttribute("authorities", allAuthorities);
        model.addAttribute("groups", userGroups);
        model.addAttribute("importerAuthoritySourceList", allAuthorities.stream().map(authorityEntry -> authorityEntry.source).collect(Collectors.toList()));
        model.addAttribute("displayBy", "all");
        model.addAttribute("username", user.getUsername());
        return "auth/authorities/list";
    }
    
    @RequestMapping("/auth/authority/{zoteroGroupId}/list")
    public String getAuthoritiesForGroup(Model model, Authentication authentication, 
            @PathVariable("zoteroGroupId") String zoteroGroupId) {
        IUser user = (IUser)authentication.getPrincipal();
        model.addAttribute("authorities", authorityService.getAuthoritiesByGroup(Long.valueOf(zoteroGroupId)));
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", zoteroGroupId);
        model.addAttribute("username", user.getUsername());
        return "auth/authorities/list";
    }
    
    @RequestMapping("/auth/authority/user/list")
    public String getAuthoritiesForUser(Model model, Authentication authentication) {
        IUser user = (IUser)authentication.getPrincipal();
        model.addAttribute("authorities", authorityService.getUserSpecificAuthorities(user));   
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", "userSpecific");
        model.addAttribute("username", user.getUsername());
        return "auth/authorities/list";
    }

    @RequestMapping("/auth/authority/{source}/list")
    public String getAuthoritiesForSource(Model model, Authentication authentication,
            @PathVariable("zoteroGroupId") String source) {
        IUser user = (IUser)authentication.getPrincipal();
        model.addAttribute("authorities", authorityService.getUserSpecificAuthorities(user));
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", source);
        model.addAttribute("username", user.getUsername());
        return "auth/authorities/list";
    }
}
