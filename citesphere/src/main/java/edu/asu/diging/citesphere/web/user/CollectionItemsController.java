package edu.asu.diging.citesphere.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;

@Controller
public class CollectionItemsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationCollectionManager collectionManager;

    public String show(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String groupId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort)
            throws GroupDoesNotExistException, ZoteroHttpStatusException {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }

        IUser user = (IUser) authentication.getPrincipal();
        CitationResults results = citationManager.getGroupItems(user, groupId, null, pageInt, sort);
        model.addAttribute("items", results.getCitations());
        model.addAttribute("total", results.getTotalResults());
        model.addAttribute("totalPages", Math.ceil(new Float(results.getTotalResults()) / new Float(zoteroPageSize)));
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("zoteroGroupId", groupId);
        model.addAttribute("citationCollections", collectionManager.getCitationCollections(user, groupId, null, pageInt, "title").getCitationCollections());

        return "auth/group/items";
    }
}
