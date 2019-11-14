package edu.asu.diging.citesphere.api.v1.user;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import edu.asu.diging.citesphere.core.user.IUserManager;

@Controller
public class CitationEntryController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/item/{itemId}")
    public ResponseEntity<String> getItem(@RequestHeader HttpHeaders headers, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId) {
        // TODO: Get logged in user, remove:
        IUser user = userManager.findByUsername("namrathaov");
        String output = "";
        try {
            ICitation citation = citationManager.getCitation(user, zoteroGroupId, itemId);
            List<String> citesphereFields = Stream.of(CitesphereSupportedFields.values())
                    .map(CitesphereSupportedFields::toString).collect(Collectors.toList());
            
            citationManager.getItemTypeFields(user, citation.getItemType())
                    .forEach(f -> citesphereFields.add(f.getFilename()));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode linkNode = mapper.createObjectNode();
            for (Field field : citation.getClass().getDeclaredFields()) {
                //TODO Process other creators separately.
                if (citesphereFields.contains(field.getName())) {
                    Field citationField = citation.getClass().getDeclaredField(field.getName());
                    citationField.setAccessible(true);
                    String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString() : "";
                    linkNode.put(field.getName(), citationValue);
                }
            }
            output = mapper.writeValueAsString(linkNode);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                logger.error("Could not access field.", e);
        } catch (GroupDoesNotExistException e) {
            return new ResponseEntity<String>("{\"error\": \"Group " + zoteroGroupId + " not found.\"}", HttpStatus.NOT_FOUND);
        } catch (CannotFindCitationException e) {
            return new ResponseEntity<String>("{\"error\": \"Item " + itemId + " not found.\"}", HttpStatus.NOT_FOUND);
        } catch(JsonProcessingException e) {
            return new ResponseEntity<String>("{\"error\": \"Error ocurred in processing JSON.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(output, HttpStatus.OK);
    }
}
