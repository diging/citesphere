package edu.asu.diging.citesphere.core.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class AsyncCitationManager {
    @Autowired
    private IZoteroManager zoteroManager;

    @Async
    public Future<ZoteroUpdateItemsStatuses> updateCitations(IUser user, String groupId, List<ICitation> citations)
            throws ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException,
            ExecutionException, JsonProcessingException {
        return new AsyncResult<ZoteroUpdateItemsStatuses>(zoteroManager.updateCitations(user, groupId, citations));
    }
}
