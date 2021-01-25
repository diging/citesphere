package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.dto.MovedItemsData;

@Controller
public class MoveItemsController {

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move", method = RequestMethod.POST)
    public ResponseEntity<String> moveItemToCollection(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody String itemsData)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException,
            CitationIsOutdatedException {
        ICitation citation;
        Gson gson = new Gson();
        MovedItemsData itemsDataDto = gson.fromJson(itemsData, MovedItemsData.class);
        for (String key : itemsDataDto.getItemIds()) {
            try {
                citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, key);
            } catch (CannotFindCitationException e) {
                return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
            }
            citationHelper.updateCitation(citation, itemsDataDto.getCollectionId(),
                    (IUser) authentication.getPrincipal());
            citationManager.updateCitation((IUser) authentication.getPrincipal(), zoteroGroupId, citation);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
