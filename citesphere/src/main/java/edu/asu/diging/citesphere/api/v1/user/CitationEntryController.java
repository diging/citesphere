package edu.asu.diging.citesphere.api.v1.user;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.zotero.impl.ZoteroTokenManager;

@Controller
public class CitationEntryController extends V1Controller {
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/item/{itemId}")
    public ResponseEntity<String> getItem(@RequestHeader HttpHeaders headers, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) throws GroupDoesNotExistException, CannotFindCitationException, JsonProcessingException {
        // TODO: Get logged in user
        ICitation citation = citationManager.getCitation(null, zoteroGroupId, itemId);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode linkNode = mapper.createObjectNode();
        //TODO remove:
        IUser user = userManager.findByUsername("namrathaov");
        if (citation != null) {
            //TODO: Add valid fields in web page
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields(user, citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            linkNode.put("fields", mapper.writeValueAsString(fields));
            //linkNode.put("citation", mapper.writeValueAsString(citation));
            // Iterate all fields of citation and check if valid item type field.
            try {
                for(String field: fields) {
                        Field citationField = citation.getClass().getDeclaredField(field);
                        citationField.setAccessible(true);
                        String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString() : "";
                        linkNode.put(field, citationValue);
                    
                }
                Field citationField = citation.getClass().getDeclaredField("otherCreators");
                String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString() : "";
                linkNode.put("otherCreators", citationValue);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                // logger.error("Could ont access field.", e);
                // let's ignore that for nonw
            }
        } else {
            return new ResponseEntity<String>("{\"error\": \"Item " + itemId + " not found.\"}", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(mapper.writeValueAsString(linkNode), HttpStatus.OK);
    }
}
