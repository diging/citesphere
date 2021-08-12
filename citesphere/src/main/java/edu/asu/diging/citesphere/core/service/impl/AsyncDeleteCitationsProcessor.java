package edu.asu.diging.citesphere.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class AsyncDeleteCitationsProcessor {

    @Autowired
    private IZoteroManager zoteroManager;

    @Async
    public Future<Map<ItemDeletionResponse, List<String>>> deleteCitations(IUser user, String groupId,
            List<String> citationIdList) throws ZoteroConnectionException, ZoteroHttpStatusException {
        return new AsyncResult<Map<ItemDeletionResponse, List<String>>>(zoteroManager.deleteMultipleItems(user, groupId,
                citationIdList, zoteroManager.getLatestGroupVersion(user, groupId)));
    }
}
