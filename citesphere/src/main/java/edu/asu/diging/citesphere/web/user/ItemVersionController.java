package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemVersionController {

    @Autowired
    private ICitationManager citationManager;

    @GetMapping("/auth/group/{zoteroGroupId}/items/{itemId}/history")
    public String getVersions(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId)
            throws AccessForbiddenException {
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("versions", citationManager.getCitationVersions((IUser)authentication.getPrincipal(), zoteroGroupId, itemId));
        return "auth/group/itemHistory";
    }
}
