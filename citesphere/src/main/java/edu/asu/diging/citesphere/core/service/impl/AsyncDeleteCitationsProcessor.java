package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class AsyncDeleteCitationsProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IZoteroManager zoteroManager;

    @Autowired
    private ICitationStore citationStore;
    
    @Autowired
    private CitationManager citationManager;

    @Async
    public Future<Map<ItemDeletionResponse, List<String>>> deleteCitations(IUser user, String groupId,
            List<String> citationIdList) throws ZoteroConnectionException, ZoteroHttpStatusException {
        AsyncResult<Map<ItemDeletionResponse, List<String>>> result = new AsyncResult<>(
                zoteroManager.deleteMultipleItems(user, groupId, citationIdList,
                        zoteroManager.getLatestGroupVersion(user, groupId)));
        result.addCallback(new ListenableFutureCallback<Map<ItemDeletionResponse, List<String>>>() {

            @Override
            public void onSuccess(Map<ItemDeletionResponse, List<String>> result) {
                for (String itemKey : result.getOrDefault(ItemDeletionResponse.SUCCESS, new ArrayList<>())) {
                    Optional<ICitation> citation = citationStore.findById(itemKey);
                    if (citation.isPresent()) {
                        citationStore.delete(citation.get());
                    }
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Error while deleting citations.", ex);
            }
        });
        return result;
    }
    
    public void hideCitations(IUser user, String groupId, List<String> citationIdList) {
        for(String item : citationIdList) {
            try {
            ICitation citation = citationManager.getCitation(user, groupId, item);
            citation.setRemoved(1);
            citationManager.updateCitation(user, groupId, citation);
//            citationStore.save(citation);
            }
            catch(Exception ex) {
                logger.error("Error while hiding citations.", ex);
            }
        }
    }
}
