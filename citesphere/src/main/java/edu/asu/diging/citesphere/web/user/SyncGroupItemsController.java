package edu.asu.diging.citesphere.web.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class SyncGroupItemsController {

    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value = {"/auth/group/{zoteroGroupId}/items/sync", "/auth/group/{zoteroGroupId}/collection/{collectionId}/items/sync"})
    public String getCitationsList(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @PathVariable(value="collectionId", required=false) String collectionId,
            @RequestParam(required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String columns) throws GroupDoesNotExistException {
        if (page == null || page.trim().isEmpty()) {
            page = "1";
        }
        citationManager.forceGroupItemsRefresh((IUser) authentication.getPrincipal(), zoteroGroupId, collectionId, new Integer(page), sort);
        if(collectionId != null && !collectionId.trim().isEmpty()) {
            return "redirect:/auth/group/{zoteroGroupId}/collection/{collectionId}/items?page=" + page + "&sort=" + sort+ "&columns=" + columns;
        }
        return "redirect:/auth/group/{zoteroGroupId}/items?page=" + page + "&sort=" + sort+ "&columns=" + columns;
    }
}
