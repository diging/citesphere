package edu.asu.diging.citesphere.web.user.authorities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AuthorityItemsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IAuthorityService authorityService;

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private IGroupManager groupManager;
    
    @Autowired
    private ICitationCollectionManager collectionManager;


    @RequestMapping("/auth/authority/items")
    public String showPage(Model model, @RequestParam("uri") String uri, 
            @RequestParam("name") String name, Authentication authentication) {
        List<IAuthorityEntry> authorityEntries = authorityService.findByUri((IUser) authentication.getPrincipal(), uri);
        
        if(authorityEntries == null || authorityEntries.isEmpty()) {
            model.addAttribute("error", "No entry found for URI: " + uri);
        } else if (authorityEntries.size() > 1) {
            logger.error("Found multiple entries for URI: " + uri);
            model.addAttribute("error", "Found multiple entries for URI: " + uri);
        } else {
            Citations citations = citationManager.findAuthorityCitations(authorityEntries.get(0), (IUser) authentication.getPrincipal());
            if (citations != null) {
                model.addAttribute("items", citations.getCitations());
                
                // Create maps to store group and collection information for each citation
                Map<String, String> groupNames = new HashMap<>();
                Map<String, String> collectionNames = new HashMap<>();
                
                // Add group and collection information for each citation
                for (ICitation citation : citations.getCitations()) {
                    String citationKey = citation.getKey();
                    
                    // Get group name
                    ICitationGroup group = groupManager.getGroup((IUser) authentication.getPrincipal(), citation.getGroup());
                    if (group != null) {
                        groupNames.put(citationKey, group.getName());
                    }
                    
                    // Get collection names if any
                    if (citation.getCollections() != null && !citation.getCollections().isEmpty()) {
                        StringBuilder collectionNamesStr = new StringBuilder();
                        for (String collectionId : citation.getCollections()) {
                            ICitationCollection collection = collectionManager.getCollection((IUser) authentication.getPrincipal(), citation.getGroup(), collectionId);
                            if (collection != null) {
                                if (collectionNamesStr.length() > 0) {
                                    collectionNamesStr.append(", ");
                                }
                                collectionNamesStr.append(collection.getName());
                            }
                        }
                        if (collectionNamesStr.length() > 0) {
                            collectionNames.put(citationKey, collectionNamesStr.toString());
                        }
                    }
                }
                
                model.addAttribute("groupNames", groupNames);
                model.addAttribute("collectionNames", collectionNames);
            } else {
                model.addAttribute("error", "This authority is not used for any citations.");
            }
        }
        model.addAttribute("authName", name.trim());
        return "auth/authorities/showItemsByName";
    }
}
