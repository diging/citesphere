package edu.asu.diging.citesphere.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.cache.impl.PageRequest;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class GetItemsManager {

    @Autowired
    private CitationManager manager;
    
    private Map<String,Future<CitationResults>> resultMap = new ConcurrentHashMap<>();
    
    
    public CitationResults getGroupItems(IUser user, String groupId, String collectionId, int page, String sortBy) throws GroupDoesNotExistException, ZoteroHttpStatusException, InterruptedException, ExecutionException {
        String key = user.getUsername() + groupId + collectionId + page + sortBy;
        if (resultMap.get(key) == null) {
            resultMap.put(key, getItemsAsync(user, groupId, collectionId, page, sortBy));
        
        } else {
            Future<CitationResults> results = resultMap.get(key);
            if (results.isDone()) {
                resultMap.remove(results);
                return results.get();
            }
        }
        return null;
        
    }
    @Async
    public Future<CitationResults> getItemsAsync(IUser user, String groupId, String collectionId, int page, String sortBy) throws GroupDoesNotExistException, ZoteroHttpStatusException {
     
        CitationResults results =     manager.getGroupItems(user, groupId, collectionId, page, sortBy);
        return new AsyncResult<CitationResults>(results);
    }
    
  
}
