package edu.asu.diging.citesphere.api.v1.user;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class CitationEntryController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = "/group/{zoteroGroupId}/item/{itemId}", produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getItem(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
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
                // TODO Process other creators separately.
                if (citesphereFields.contains(field.getName())) {
                    Field citationField = citation.getClass().getDeclaredField(field.getName());
                    citationField.setAccessible(true);
                    String citationValue = citationField.get(citation) != null ? citationField.get(citation).toString()
                            : "";
                    linkNode.put(field.getName(), citationValue);
                }
            }
            output = mapper.writeValueAsString(linkNode);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            logger.error("Could not access field.", e);
        } catch (GroupDoesNotExistException e) {
            logger.error("Group does not exist.", e);
            return new ResponseEntity<String>("{\"error\": \"Group " + zoteroGroupId + " not found.\"}",
                    HttpStatus.NOT_FOUND);
        } catch (CannotFindCitationException e) {
            logger.error("Cition not found.", e);
            return new ResponseEntity<String>("{\"error\": \"Item " + itemId + " not found.\"}", HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            logger.error("JSON creation issue.", e);
            return new ResponseEntity<String>("{\"error\": \"Error ocurred in processing JSON.\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AccessForbiddenException e) {
            logger.error("Access forbidden.", e);
            return new ResponseEntity<String>("{\"error\": \"Access forbidden to item " + itemId + ".\"}", HttpStatus.FORBIDDEN);
        } catch (ZoteroHttpStatusException e) {
            logger.error("Zotero threw exception.", e);
            return new ResponseEntity<String>("{\"error\": \"" + e.getMessage() + ".\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(output, HttpStatus.OK);
    }
}
