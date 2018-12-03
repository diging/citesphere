package edu.asu.diging.citesphere.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Controller
public class GroupItemsController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IZoteroManager zoteroManager;

    @RequestMapping("/auth/group/{zoteroGroupId}/items")
    public String show(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String groupId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        
        IUser user = (IUser) authentication.getPrincipal();
        model.addAttribute("items", zoteroManager.getGroupItems(user, groupId, pageInt));
        model.addAttribute("zoteroGroupId", groupId);
        
        return "auth/group/items";
    }
}
