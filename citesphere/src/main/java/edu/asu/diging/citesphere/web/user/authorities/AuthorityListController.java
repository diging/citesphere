package edu.asu.diging.citesphere.web.user.authorities;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AuthorityListController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${_authority_page_size}")
    private Integer authorityPageSize;
    
    @Autowired
    private IAuthorityService authorityService;
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping("/auth/authority/list")
    public String list(Model model, Authentication authentication,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.error("Trying to access invalid page number: ", ex);
        }
        pageInt = (pageInt - 1) < 0 ? 0 : pageInt - 1;
        IUser user = (IUser)authentication.getPrincipal();
        List<ICitationGroup> userGroups = citationManager.getGroups((IUser) authentication.getPrincipal());
        Page<IAuthorityEntry> authoritiesPage = authorityService.getAll(user,
                userGroups.stream().map(group -> group.getGroupId()).collect(Collectors.toList()),pageInt, authorityPageSize);
        List<IAuthorityEntry> authorities = authoritiesPage.getContent();
        model.addAttribute("authorities", authorities);
        model.addAttribute("groups", userGroups);
        model.addAttribute("displayBy", "all");
        model.addAttribute("username", user.getUsername());
        model.addAttribute("total", authoritiesPage.getTotalElements());
        model.addAttribute("totalPages", authoritiesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "auth/authorities/list";
    }
    
    @RequestMapping("/auth/authority/{zoteroGroupId}/list")
    public String getAuthoritiesForGroup(Model model, Authentication authentication, 
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.error("Trying to access invalid page number: ", ex);
        }
        pageInt = (pageInt - 1) < 0 ? 0 : pageInt - 1;
        IUser user = (IUser)authentication.getPrincipal();
        Page<IAuthorityEntry> authoritiesPage = authorityService.getAuthoritiesByGroup(Long.valueOf(zoteroGroupId), pageInt, authorityPageSize);
        model.addAttribute("authorities", authoritiesPage.getContent());
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", zoteroGroupId);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("total", authoritiesPage.getTotalElements());
        model.addAttribute("totalPages", authoritiesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "auth/authorities/list";
    }
    
    @RequestMapping("/auth/authority/user/list")
    public String getAuthoritiesForUser(Model model, Authentication authentication,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.error("Trying to access invalid page number: ", ex);
        }
        pageInt = (pageInt - 1) < 0 ? 0 : pageInt - 1;
        IUser user = (IUser)authentication.getPrincipal();
        Page<IAuthorityEntry> authoritiesPage = authorityService.getUserSpecificAuthorities(user, pageInt, authorityPageSize);
        model.addAttribute("authorities", authoritiesPage.getContent());   
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        model.addAttribute("displayBy", "userSpecific");
        model.addAttribute("username", user.getUsername());
        model.addAttribute("total", authoritiesPage.getTotalElements());
        model.addAttribute("totalPages", authoritiesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "auth/authorities/list";
    }
}
