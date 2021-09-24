package edu.asu.diging.citesphere.web.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.async.AsyncDeleteCitationsResponse;
import edu.asu.diging.citesphere.core.service.impl.AsyncCitationManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class DeleteCitationController {

    @Autowired
    private AsyncCitationManager asyncCitationManager;

    @RequestMapping(value = "/auth/group/{groupId}/references/delete", method = RequestMethod.POST)
    public @ResponseBody AsyncDeleteCitationsResponse delete(Authentication authentication,
            @PathVariable("groupId") String groupId,
            @RequestParam(value = "citationList", required = false) List<String> citationList)
            throws ZoteroConnectionException, ZoteroHttpStatusException, GroupDoesNotExistException {
        return asyncCitationManager.deleteCitations((IUser) authentication.getPrincipal(), groupId, citationList);
    }

    @RequestMapping("/auth/group/{zoteroGroupId}/references/delete/{taskID}/status")
    public @ResponseBody AsyncDeleteCitationsResponse getMoveItemsStatus(
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("taskID") String taskID)
            throws Exception {
        return asyncCitationManager.getDeleteCitationsResponse(taskID);
    }

}
