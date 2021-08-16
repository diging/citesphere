package edu.asu.diging.citesphere.web.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.ICitationVersionManager;
import edu.asu.diging.citesphere.factory.impl.DateParser;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemVersionsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationVersionManager citationVersionManager;

    @Autowired
    private DateParser dateParser;

    @GetMapping("/auth/group/{zoteroGroupId}/items/{itemId}/history")
    public String getVersions(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(defaultValue = "1", required = false, value = "page") int page,
            @RequestParam(defaultValue = "10", required = false, value = "pageSize") int pageSize)
            throws AccessForbiddenException {
        List<CitationVersion> versions;
        ICitation citation;
        try {
            versions = citationVersionManager.getCitationVersions((IUser) authentication.getPrincipal(), zoteroGroupId, itemId,
                    page - 1, pageSize);
            citation = citationManager.getCitation(itemId);
        } catch (GroupDoesNotExistException e) {
            logger.error("Group with id {} does not exist", zoteroGroupId);
            return "error/404";
        }

        if (citation != null) {
            model.addAttribute("title", citation.getTitle());
            model.addAttribute("authors", citation.getAuthors());
            if (citation.getDateFreetext() != null && !citation.getDateFreetext().isEmpty()) {
                model.addAttribute("year", dateParser.parse(citation.getDateFreetext()).getYear());
            }
        }
        model.addAttribute("itemKey", itemId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",
                Math.max(1, citationVersionManager.getTotalCitationVersionPages(zoteroGroupId, itemId, pageSize)));
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("versions", versions);
        return "auth/group/itemVersions";
    }
}
