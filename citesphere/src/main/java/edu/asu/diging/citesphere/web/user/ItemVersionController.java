package edu.asu.diging.citesphere.web.user;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.ICitationVersionManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemVersionController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationVersionManager citationVersionManager;

    @GetMapping("/auth/group/{zoteroGroupId}/items/{itemId}/history/{version}/version")
    public String getVersions(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @PathVariable("version") Long version,
            @RequestParam(defaultValue = "1", required = false, value = "page") int page,
            @RequestParam(required = false, value = "searchTerm") String searchTerm,
            @RequestParam(defaultValue = "1", required = false, value = "itemsPage") int itemsPage,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) {
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        ICitation citation;
        try {
            citation = citationVersionManager.getCitationVersion((IUser) authentication.getPrincipal(), zoteroGroupId,
                    itemId, version);
        } catch (GroupDoesNotExistException e) {
            logger.error("Group with id {} does not exist", zoteroGroupId, e);
            return "error/404";
        }
        if (citation != null) {
            model.addAttribute("itemVersion", version);
            model.addAttribute("page", page);
            model.addAttribute("citation", citation);
            List<String> fields = citationManager
                    .getItemTypeFields((IUser) authentication.getPrincipal(), citation.getItemType()).stream()
                    .map(f -> f.getFilename()).collect(Collectors.toList());
            model.addAttribute("fields", fields);
            model.addAttribute("searchTerm", searchTerm);
            model.addAttribute("itemsPage", itemsPage);
            model.addAttribute("sortBy", sortBy);
        } else {
            return "error/404";
        }
        return "auth/group/itemVersion";
    }
}
