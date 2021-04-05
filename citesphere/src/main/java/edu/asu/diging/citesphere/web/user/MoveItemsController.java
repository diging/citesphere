package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.core.zotero.ZoteroUpdateItemsResponse;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.dto.CitationStatusesData;
import edu.asu.diging.citesphere.web.user.dto.MoveItemsRequest;

@Controller
public class MoveItemsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private AsyncTaskProcessor<ZoteroUpdateItemsStatuses> asyncTaskProcessor;

    @Autowired
    private ICitationHelper citationHelper;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move", method = RequestMethod.POST)
    public @ResponseBody String moveItemsToCollection(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody String itemsData)
            throws Exception {
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
        
        AsyncTaskResponse<ZoteroUpdateItemsStatuses> asyncResponse;
        try {
            asyncResponse = asyncTaskProcessor.submitTask(() -> citationManager.updateCitations((IUser) authentication.getPrincipal(),
                    zoteroGroupId, citations));
        } catch (Exception e) {
            logger.error("Unable to move citations.", e);
            throw e;
        }
                
        return gson.toJson(asyncResponse, AsyncTaskResponse.class);
    }
    
    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move/status", method = RequestMethod.POST)
    public @ResponseBody AsyncTaskResponse<ZoteroUpdateItemsStatuses> getMoveItemsStatus(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody String taskID) throws Exception {
        return asyncTaskProcessor.getResponse(taskID);
    }
    
    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move/cleartask", method = RequestMethod.POST)
    public @ResponseBody Boolean clearTask(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody String taskID) {
        return asyncTaskProcessor.clearTask(taskID);
    }
    
    
}