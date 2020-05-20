package edu.asu.diging.citesphere.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroTokenManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class HomeController {

    @Autowired
    private IZoteroTokenManager tokenManager;

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = "/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            IUser user = (IUser) authentication.getPrincipal();
            boolean isConnected = tokenManager.getToken(user) != null;
            model.addAttribute("isZoteroConnected", isConnected);
            model.addAttribute("user", user);
        }
        
        return "home";
    }
    
    @RequestMapping(value = "/groups")
    public @ResponseBody Map<Long, Long> getGroups(Authentication authentication) {
        IUser user = (IUser) authentication.getPrincipal();
        boolean isConnected = tokenManager.getToken(user) != null;
        if (isConnected) {
            return citationManager.getGroupsId(user);
        }
        return new HashMap<Long, Long>(); 
    }
    
    @RequestMapping(value = "/group/{groupId}/info")
    public @ResponseBody ICitationGroup getGroupsInfo(Authentication authentication,
            @PathVariable("groupId") String groupId,
            @RequestParam(required = true, value = "version") String version) {
        if (groupId != null && groupId != "") {
            return citationManager.getGroupInfo((IUser) authentication.getPrincipal(), Long.valueOf(groupId), Long.valueOf(version));
        }
        return new CitationGroup();
    }
    
    @RequestMapping(value ="/login")
    public String login() {
        return "login";
    }
}
