package edu.asu.diging.citesphere.web.user.authorities;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.authority.IAuthorityItemsService;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AuthorityItemsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<IAuthorityEntry> userEntries = new ArrayList();

    @Autowired
    private IAuthorityService authorityService;

    @Autowired
    private IAuthorityItemsService authorityItemsService;


    @RequestMapping("/auth/authority/items")
    public String showPage(Model model, @RequestParam("uri") String uri, Authentication authentication) {
        userEntries = authorityService.findByUri((IUser) authentication.getPrincipal(), uri);
        
        if(userEntries == null || userEntries.isEmpty()) {
            logger.error("No entry found for URI: " + uri);
            model.addAttribute("error", "No entry found for URI: " + uri);
        } else if (userEntries.size() > 1) {
            logger.error("Found multiple entries for URI: " + uri);
            model.addAttribute("error", "Found multiple entries for URI: " + uri);
        } else {
            Citations citations = authorityItemsService.findAuthorityItems(userEntries.get(0));
            if (citations != null) {
                model.addAttribute("items", citations.getCitations());
            } else {
                model.addAttribute("items", new ArrayList<ICitation>());
            }
        }
        return "auth/authorities/showItemsByName";
    }
}
