package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.JobRepository;
import edu.asu.diging.citesphere.core.service.IAsyncCitationProcessor;
import edu.asu.diging.citesphere.core.zotero.DeletedZoteroElements;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.core.zotero.ZoteroCollectionsResponse;
import edu.asu.diging.citesphere.core.zotero.ZoteroGroupItemsResponse;
import edu.asu.diging.citesphere.data.bib.CitationCollectionRepository;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.CitationRepository;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollection;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class AsyncCitationProcessor implements IAsyncCitationProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IZoteroManager zoteroManager;

    @Autowired
    private CitationRepository citationRepo;
    
    @Autowired
    private CitationGroupRepository groupRepo;
    
    @Autowired
    private CitationCollectionRepository collectionRepo;
    
    @Autowired
    private JobRepository jobRepo;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IAsyncCitationProcessor#loadCitations(edu.asu.diging.citesphere.user.IUser, edu.asu.diging.citesphere.model.bib.ICitationGroup, java.lang.String, java.lang.String)
     */
    @Override
    @Async
    public void loadCitations(IUser user, ICitationGroup group, String collectionId, String sortBy) {
        GroupSyncJob job = new GroupSyncJob();
        job.setCreatedOn(OffsetDateTime.now());
        job.setGroupId(group.getGroupId() + "");
        job.setStatus(JobStatus.PREPARED);
        jobRepo.save(job);
        
        DeletedZoteroElements deletedElements = zoteroManager.getDeletedElements(user, group.getGroupId() + "", group.getContentVersion());
        Map<String, Long> versions = zoteroManager.getGroupItemVersions(user, group.getGroupId() + "",
                group.getContentVersion());
        Map<String, Long> collectionVersions = zoteroManager.getCollectionsVersions(user, group.getGroupId() + "", group.getContentVersion() + "");
        
        
        job.setTotal(versions.size() + collectionVersions.size() + (deletedElements.getItems() != null ? deletedElements.getItems().size() : 0));
        job.setStatus(JobStatus.STARTED);
        jobRepo.save(job);
        
        AtomicInteger counter = new AtomicInteger();
        long version = syncCitations(user, group, job, versions, counter);
        long collectionVersion = syncCollections(user, group, job, collectionVersions, counter);
        if (collectionVersion > -1) {
            version = collectionVersion;
        }
        
        
        removeDeletedItems(deletedElements, job);
        
        group.setContentVersion(version);
        groupRepo.save((CitationGroup)group);
        
        job.setCurrent(counter.longValue());
        job.setStatus(JobStatus.DONE);
        job.setFinishedOn(OffsetDateTime.now());
        jobRepo.save(job);
    }

    private long syncCitations(IUser user, ICitationGroup group, GroupSyncJob job, Map<String, Long> versions,
            AtomicInteger counter) {
        List<String> keysToRetrieve = new ArrayList<>();
        long version = 0;
        for (String key : versions.keySet()) {
            Optional<ICitation> citation = citationRepo.findByKey(key);
            
            if (citation.isPresent()) {
                if (citation.get().getVersion() != versions.get(key)) {
                    keysToRetrieve.add(key);
                }
            } else {
                keysToRetrieve.add(key);
            }
            counter.incrementAndGet();
            if (counter.intValue()%50 == 0) {
                version = retrieveCitations(user, group, keysToRetrieve);
                keysToRetrieve = new ArrayList<>();
            }
            job.setCurrent(counter.intValue());
            jobRepo.save(job);
        }
        
        if (!keysToRetrieve.isEmpty()) {
            version = retrieveCitations(user, group, keysToRetrieve);
        }
        
        return version;
    }
    
    private long syncCollections(IUser user, ICitationGroup group, GroupSyncJob job, Map<String, Long> versions,
            AtomicInteger counter) {
        List<String> keysToRetrieve = new ArrayList<>();
        long version = -1;
        for (String key : versions.keySet()) {
            Optional<ICitationCollection> collection = collectionRepo.findByKey(key);
            
            if (collection.isPresent()) {
                if (collection.get().getVersion() != versions.get(key)) {
                    keysToRetrieve.add(key);
                }
            } else {
                keysToRetrieve.add(key);
            }
            counter.incrementAndGet();
            if (counter.intValue()%50 == 0) {
                version = retrieveCollections(user, group, keysToRetrieve);
                keysToRetrieve = new ArrayList<>();
            }
            job.setCurrent(counter.intValue());
            jobRepo.save(job);
        }
        
        if (!keysToRetrieve.isEmpty()) {
            version = retrieveCollections(user, group, keysToRetrieve);
        }
        
        return version;
    }

    private void removeDeletedItems(DeletedZoteroElements deletedElements, GroupSyncJob job) {
        if (deletedElements.getItems() != null) {
            for(String key : deletedElements.getItems()) {
                Optional<ICitation> citation = citationRepo.findByKey(key);
                if (citation.isPresent()) {
                    citationRepo.delete((Citation)citation.get());
                }
                job.setCurrent(job.getCurrent() + 1);
                jobRepo.save(job);
            }
        }
    }

    private long retrieveCitations(IUser user, ICitationGroup group, List<String> keysToRetrieve) {
        ZoteroGroupItemsResponse retrievedCitations = zoteroManager.getGroupItemsByKey(user, group.getGroupId() + "", keysToRetrieve);
        retrievedCitations.getCitations().forEach(c -> storeCitation(c));
        return retrievedCitations.getContentVersion();
    }
    
    private long retrieveCollections(IUser user, ICitationGroup group, List<String> keysToRetrieve) {
        ZoteroCollectionsResponse response = zoteroManager.getCollectionsByKey(user, group.getGroupId() + "", keysToRetrieve);
        response.getCollections().forEach(c -> storeCitationCollection(c));
        return response.getContentVersion();
    }

    private void storeCitation(ICitation citation) {
        Optional<ICitation> optional = citationRepo.findByKey(citation.getKey());
        if (optional.isPresent()) {
            citationRepo.delete((Citation)optional.get());
        }
        citationRepo.save((Citation)citation);
    }
    
    private void storeCitationCollection(ICitationCollection collection) {
        Optional<ICitationCollection> optional = collectionRepo.findByKey(collection.getKey());
        if (optional.isPresent()) {
            collectionRepo.delete((CitationCollection)optional.get());
        }
        collectionRepo.save((CitationCollection)collection);
    }

}
