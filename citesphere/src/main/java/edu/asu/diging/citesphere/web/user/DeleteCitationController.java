package edu.asu.diging.citesphere.web.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class DeleteCitationController {

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = "/auth/group/{groupId}/references/delete", method = RequestMethod.POST)
    public ResponseEntity<List<ItemDeletionResponse>> delete(Authentication authentication,
            @PathVariable("groupId") String groupId,
            @RequestParam(value = "citationList", required = false) List<String> citationList)
            throws ZoteroConnectionException, ZoteroHttpStatusException, GroupDoesNotExistException {
        List<ItemDeletionResponse> zoteroResponse = citationManager.deleteCitations((IUser) authentication.getPrincipal(), groupId, citationList);
        for (ItemDeletionResponse response : zoteroResponse) {
            if (!response.equals(ItemDeletionResponse.SUCCESS)) {
                throw new ZoteroHttpStatusException("Could not delete items. Error: " + response.getValue() + " " + response.getDescription());
            }
        }
        return new ResponseEntity<List<ItemDeletionResponse>>(zoteroResponse, HttpStatus.OK);
    }
}
