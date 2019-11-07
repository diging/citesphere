package edu.asu.diging.citesphere.web.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.service.ICitationManager;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) throws GroupDoesNotExistException, CannotFindCitationException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        
        if (citation != null) {
            model.addAttribute("citation", citation);
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            model.addAttribute("fields", fields);
        }
        return "auth/group/items/item";
    }
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/item/{itemId}")
    public ResponseEntity<String> displayItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) throws GroupDoesNotExistException, CannotFindCitationException, JsonProcessingException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode linkNode = mapper.createObjectNode();
        linkNode.put("zoteroGroupId", zoteroGroupId);
        if (citation != null) {
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            linkNode.put("citation", mapper.writeValueAsString(citation));
            linkNode.put("fields", mapper.writeValueAsString(fields));
        } else {
            return new ResponseEntity<String>("{\"error\": \"Item " + itemId + " not found.\"}", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(mapper.writeValueAsString(linkNode), HttpStatus.OK);
    }
}
