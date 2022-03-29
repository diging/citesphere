package edu.asu.diging.citesphere.web.user.authorities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ListAuthoritiesController {

    @Autowired
    private IAuthorityService authorityService;

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping("/auth/authority/{authorityId}/listByName")
    public String showPage(Model model, @PathVariable("authorityId") String authorityId, Authentication authentication)
    		throws GroupDoesNotExistException, ZoteroHttpStatusException {
        IAuthorityEntry entry = authorityService.find(authorityId);
        IUser user = (IUser) authentication.getPrincipal();
        List<ICitationGroup> groups = citationManager.getGroups(user);
        List<String> groupIds = new ArrayList<String>();
        for (int i = 0; i < groups.size(); i++) {
            groupIds.add(String.valueOf(groups.get(i).getGroupId()));
        }
        CitationResults results = null;
        Set<ICitation> citations = new HashSet<ICitation>();
        for (String groupId : groupIds) {
            results = citationManager.getGroupItems(user, groupId, null, 1, "title");
            for (ICitation citation : results.getCitations()) {
                String name = entry.getName();
                Set<IPerson> authors = citation.getAuthors();
                for (IPerson person : authors) {
                    if (person.getName().equals(" " + name)) {
                        citations.add(citation);
                        break;
                    }
                }
                Set<IPerson> editors = citation.getEditors();
                for (IPerson person : editors) {
                    if (person.getName().equals(" " + name) && !citations.contains(citation)) {
                        citations.add(citation);
                        break;
                    }
                }
                Set<ICreator> otherCreators = citation.getOtherCreators();
                Set<IPerson> creators = new HashSet<IPerson>();
                for (ICreator creator : otherCreators) {
                    creators.add(creator.getPerson());
                }
                for (IPerson person : creators) {
                    if (person.getName().equals(" " + name) && !citations.contains(citation)) {
                        citations.add(citation);
                        break;
                    }
                }
            }
        }
        model.addAttribute("items", citations);
        model.addAttribute("name", entry.getName());
        return "auth/authorities/listAuthorities";
    }
}
