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
    private ICitationHelper citationHelper;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/move", method = RequestMethod.POST)
    public @ResponseBody String moveItemToCollection(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @RequestBody String itemsData)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException,
            CitationIsOutdatedException, InterruptedException, ExecutionException {
        Gson gson = new Gson();
        MoveItemsRequest itemsDataDto = gson.fromJson(itemsData, MoveItemsRequest.class);
        List<ICitation> citations = new ArrayList<>();
        ICitation citation;
        List<String> notMovedCitations = new ArrayList<>();
        for (String key : itemsDataDto.getItemIds()) {
            try {
                citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId, key);
                citations.add(citation);
            } catch (CannotFindCitationException e) {
                logger.error("Cannot find citation.", e);
                notMovedCitations.add(key);
                continue;
            }
            citationHelper.addCollection(citation, itemsDataDto.getCollectionId(),
                    (IUser) authentication.getPrincipal());
        }
        ZoteroUpdateItemsStatuses response = citationManager.updateCitations((IUser) authentication.getPrincipal(),
                zoteroGroupId, citations);
        CitationStatusesData statusesDto = new CitationStatusesData();
        statusesDto.setMovedCitations(response.getSuccessItems());
        for (String failedItemKey : response.getFailedItems()) {
            notMovedCitations.add(failedItemKey);
        }
        statusesDto.setNotMovedCitations(notMovedCitations);
        return gson.toJson(statusesDto, CitationStatusesData.class);
    }
}