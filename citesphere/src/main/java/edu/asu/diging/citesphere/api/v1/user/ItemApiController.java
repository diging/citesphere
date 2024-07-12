package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.ItemDetails;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemApiController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;

    @GetMapping(value = "/groups/{groupId}/items/{item}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getItem(@PathVariable("groupId") String groupId, @PathVariable("item") String itemKey,
            Principal principal) throws GroupDoesNotExistException, DuplicateKeyException {
        IUser user = userManager.findByUsername(principal.getName());

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        ICitation item;
        List<ICitation> attachments;
        try {
            item = citationManager.getCitation(user, groupId, itemKey);
            attachments = citationManager.getAttachments(user, groupId, itemKey);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        } catch (CannotFindCitationException | ZoteroHttpStatusException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        ItemDetails response = new ItemDetails();
        response.setItem(item);
        response.setAttachments(attachments.stream().map(ICitation::getTitle).collect(Collectors.toList()));

        String jsonResponse = "";
        try {
            jsonResponse = new ObjectMapper().writeValueAsString(response);
        } catch (IOException e) {
            logger.error("Unable to process JSON response ", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
    }
}
