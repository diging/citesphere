package edu.asu.diging.citesphere.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationVersionException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationVersionManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class RevertCitationController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ICitationVersionManager citationVersionManager;

    @RequestMapping("/auth/group/{zoteroGroupId}/items/{itemId}/revert/version/{version}")
    public String revertCitationVersion(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @PathVariable("version") Long version) {

        try {
            citationVersionManager.revertCitationVersion((IUser) authentication.getPrincipal(), zoteroGroupId, itemId,
                    version);
            return "redirect:/auth/group/" + zoteroGroupId + "/items/" + itemId;
        } catch (GroupDoesNotExistException | ZoteroConnectionException | CitationIsOutdatedException
                | ZoteroHttpStatusException | CannotFindCitationVersionException | CannotFindCitationException e) {
            logger.error(e.getMessage());
            return "error/404";
        }

    }

}
