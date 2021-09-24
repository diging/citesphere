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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.async.AsyncUpdateCitationsResponse;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.impl.AsyncCitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
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
    private ICitationCollectionManager collectionManager;

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/items/move",
            "/auth/group/{zoteroGroupId}/collection/{collectionId}/items/move" }, method = RequestMethod.POST)
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

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/items/move/{taskID}/status",
            "/auth/group/{zoteroGroupId}/collection/{collectionId}/items/move/{taskID}/status" })
    public @ResponseBody AsyncUpdateCitationsResponse getMoveItemsStatus(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("taskID") String taskID)
            throws Exception {
        return asyncCitationManager.getUpdateCitationsResponse(taskID);
    }

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/items/move/task/{taskID}/clear",
            "/auth/group/{zoteroGroupId}/collection/{collectionId}/items/move/task/{taskID}/clear" }, method = RequestMethod.POST)
    public @ResponseBody void clearTask(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("taskID") String taskID) {
        asyncCitationManager.clearUpdateTask(taskID);
    }

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/items/move/{targetCollectionId}/sync/start",
            "/auth/group/{zoteroGroupId}/collection/{parentcollectionId}/items/move/{targetCollectionId}/sync/start" })
    public @ResponseBody Sync startSync(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("targetCollectionId") String collectionId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        try {
            citationManager.getGroupItems((IUser) authentication.getPrincipal(), zoteroGroupId, collectionId,
                    new Integer(page), null);
            Sync sync = new Sync();
            sync.setStatus("sync-started");
            return sync;
        } catch (ZoteroHttpStatusException e) {
            logger.error("Zotero HTTP status exception occured while syncing ", e);
            return null;
        } catch (GroupDoesNotExistException e) {
            logger.error("Group does not exists exception occured while syncing", e);
            return null;
        }
    }

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/items/move/{targetCollectionId}/totalItems",
            "/auth/group/{zoteroGroupId}/collection/{parentcollectionId}/items/move/{targetCollectionId}/totalItems" })
    public @ResponseBody Long getTotalCitationsCollection(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("targetCollectionId") String collectionId) {
        ICitationCollection collection = collectionManager.getCollection((IUser) authentication.getPrincipal(),
                zoteroGroupId, collectionId);
        return collection.getNumberOfItems();
    }

    class Sync {
        String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}