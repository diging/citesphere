package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.dto.MoveItemsRequest;

@Controller
public class MoveItemsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private AsyncCitationManager asyncCitationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @Autowired
    private IZoteroManager zoteroManager;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move", method = RequestMethod.POST)
    public @ResponseBody String moveItemsToCollection(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody String itemsData) throws Exception {
        Gson gson = new Gson();
        MoveItemsRequest itemsDataDto = gson.fromJson(itemsData, MoveItemsRequest.class);
        List<ICitation> citations = new ArrayList<>();
        ICitation citation;
        for (String key : itemsDataDto.getItemIds()) {
            try {
                citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, key);
                citations.add(citation);
            } catch (CannotFindCitationException e) {
                logger.error("Cannot find citation.", e);
                continue;
            }
            citationHelper.addCollection(citation, itemsDataDto.getCollectionId(),
                    (IUser) authentication.getPrincipal());
        }
        AsyncUpdateCitationsResponse asyncResponse = asyncCitationManager
                .updateCitations((IUser) authentication.getPrincipal(), zoteroGroupId, citations);
        return gson.toJson(asyncResponse, AsyncUpdateCitationsResponse.class);
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move/{taskID}/status")
    public @ResponseBody AsyncUpdateCitationsResponse getMoveItemsStatus(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("taskID") String taskID)
            throws Exception {
        return asyncCitationManager.getUpdateCitationsResponse(taskID);
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move/task/{taskID}/clear", method = RequestMethod.POST)
    public @ResponseBody void clearTask(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("taskID") String taskID) {
        asyncCitationManager.clearTask(taskID);
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move/{collectionId}/totalItems")
    public @ResponseBody Long getTotalCitationsCollection(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("collectionId") String collectionId) {
        try {
            CitationResults results = zoteroManager.getCollectionItems((IUser) authentication.getPrincipal(),
                    zoteroGroupId, collectionId, 1, null, null);
            return results.getTotalResults();
        } catch (ZoteroHttpStatusException e) {
            logger.error("Exception occured", e);
            return null;
        }
    }

}