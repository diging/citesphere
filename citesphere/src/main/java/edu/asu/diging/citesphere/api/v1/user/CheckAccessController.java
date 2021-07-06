package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;

@Controller
public class CheckAccessController extends V1Controller {

    @Autowired
    private ICitationStore citationStore;
    
    @Autowired
    private IGroupManager groupManager;

    @RequestMapping(value = { "/files/giles/{documentId}/access/check" }, produces = {
        MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> checkAccess(@PathVariable("documentId") String documentId, @RequestParam("username") String username, Principal principal) {
        
        List<String> authorities = ((OAuth2Authentication)principal).getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        if (!authorities.contains(Role.TRUSTED_CLIENT.toString())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        if (username == null || username.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
            
        List<ICitation> citations = citationStore.findByGilesDocumentId(documentId);
        for (ICitation citation : citations) {
            String groupId = citation.getGroup();
            List<ICitationGroup> groups = groupManager.getGroupInstancesForGroupId(groupId);
            // if there is a group that contains the citation and the user has access return 
            if (groups.stream().anyMatch(g -> g.getUsers().contains(username))) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        
        return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
    }
}
