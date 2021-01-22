package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.JobRepository;
import edu.asu.diging.citesphere.core.service.IAsyncCitationProcessor;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
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

    @Autowired
    private ISyncJobManager jobManager;
    
    private List<JobStatus> inactiveJobStatuses;
    
    @PostConstruct
    public void init() {
        inactiveJobStatuses = new ArrayList<>();
        inactiveJobStatuses.add(JobStatus.CANCELED);
        inactiveJobStatuses.add(JobStatus.DONE);
        inactiveJobStatuses.add(JobStatus.FAILURE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.impl.IAsyncCitationProcessor#
     * loadCitations(edu.asu.diging.citesphere.user.IUser,
     * edu.asu.diging.citesphere.model.bib.ICitationGroup, java.lang.String,
     * java.lang.String)
     */
    @Override
    @Async
    public void sync(IUser user, String groupId, long contentVersion, String collectionId) {
        GroupSyncJob prevJob = jobManager.getMostRecentJob(groupId + "");
        // it's un-intuitive to test for not inactive statuses here, but it's more likely we'll add
        // more activate job statuses than inactive ones, so it's less error prone to use the list that
        // is less likely to change.
        if (prevJob != null &&  !inactiveJobStatuses.contains(prevJob.getStatus())) {
            // there is already a job running, let's not start another one
            return;
        }
 
        GroupSyncJob job = new GroupSyncJob();
        job.setCreatedOn(OffsetDateTime.now());
        job.setGroupId(groupId + "");
        job.setStatus(JobStatus.PREPARED);
        jobRepo.save(job);
        jobManager.addJob(job);
  
        // we'll retrieve the latest group version first in case there are more changes
        // in between
        // this way the group version can be out-dated and trigger another sync next
        // time
        long groupVersion = zoteroManager.getLatestGroupVersion(user, groupId + "");
        DeletedZoteroElements deletedElements = zoteroManager.getDeletedElements(user, groupId + "",
                contentVersion);
        Map<String, Long> versions = zoteroManager.getGroupItemsVersions(user, groupId + "",
                contentVersion, true);
        Map<String, Long> collectionVersions = zoteroManager.getCollectionsVersions(user, groupId + "",
                contentVersion + "");

        job.setTotal(versions.size() + collectionVersions.size()
                + (deletedElements.getItems() != null ? deletedElements.getItems().size() : 0));
        job.setStatus(JobStatus.STARTED);
        jobRepo.save(job);

        AtomicInteger counter = new AtomicInteger();
        syncCitations(user, groupId, job, versions, counter);
        syncCollections(user, groupId, job, collectionVersions, groupVersion, counter);

        removeDeletedItems(deletedElements, job);

        
        // while this thread has been running, the group might have been updated by another thread
        // so, we have to make sure there is no group with the same group id but other object id
        // or we'll end up with two groups with the same group id.
        Optional<ICitationGroup> group = groupRepo.findFirstByGroupId(new Long(groupId));
        if (group.isPresent()) {
            group.get().setContentVersion(groupVersion);
            groupRepo.save((CitationGroup) group.get());
        }

        job.setStatus(JobStatus.DONE);
        job.setFinishedOn(OffsetDateTime.now());
        jobRepo.save(job);     
    }

    private void syncCitations(IUser user, String groupId, GroupSyncJob job, Map<String, Long> versions,
            AtomicInteger counter) {
        List<String> keysToRetrieve = new ArrayList<>();
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
            if (counter.intValue() % 50 == 0) {
                retrieveCitations(user, groupId, keysToRetrieve);
                keysToRetrieve = new ArrayList<>();
            }
            job.setCurrent(counter.intValue());
            jobRepo.save(job);
        }

        if (!keysToRetrieve.isEmpty()) {
            retrieveCitations(user, groupId, keysToRetrieve);
        }
    }
    // contentVersion of collections not set
    private void syncCollections(IUser user, String groupId, GroupSyncJob job, Map<String, Long> versions,
            long groupVersion, AtomicInteger counter) {
        List<String> keysToRetrieve = new ArrayList<>();
        List<ICitationCollection> collections = collectionRepo.findByGroupId(groupId);
        Set<String> keys = collections.stream().map(c -> c.getKey()).collect(Collectors.toSet());
        keys.addAll(versions.keySet());
        
        for (String key : keys) {
            ICitationCollection collection = collectionRepo.findByKeyAndGroupId(key, groupId);
            if (collection == null || (versions.containsKey(key) && collection.getVersion() != versions.get(key))
                    || collection.getContentVersion() != groupVersion) {
                keysToRetrieve.add(key);
            }
            counter.incrementAndGet();
            if (counter.intValue() % 50 == 0) {
                retrieveCollections(user, groupId, keysToRetrieve);
                keysToRetrieve = new ArrayList<>();
            }
            job.setCurrent(counter.intValue());
            jobRepo.save(job);
        }

        if (!keysToRetrieve.isEmpty()) {
            retrieveCollections(user, groupId, keysToRetrieve);
        }
    }

    private void removeDeletedItems(DeletedZoteroElements deletedElements, GroupSyncJob job) {
        if (deletedElements.getItems() != null) {
            for (String key : deletedElements.getItems()) {
                Optional<ICitation> citation = citationRepo.findByKey(key);
                if (citation.isPresent()) {
                    citationRepo.delete((Citation) citation.get());
                }
                job.setCurrent(job.getCurrent() + 1);
                jobRepo.save(job);
            }
        }
    }

    private long retrieveCitations(IUser user, String groupId, List<String> keysToRetrieve) {
        try {
            // wait 1 second to not send too many requests to Zotero
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Could not wait.", e);
        }
        ZoteroGroupItemsResponse retrievedCitations = zoteroManager.getGroupItemsByKey(user, groupId,
                keysToRetrieve, true);
        retrievedCitations.getCitations().forEach(c -> storeCitation(c));
        return retrievedCitations.getContentVersion();
    }

    private long retrieveCollections(IUser user, String groupId, List<String> keysToRetrieve) {
        try {
            // wait 1 second to not send too many requests to Zotero
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Could not wait.", e);
        }
        ZoteroCollectionsResponse response = zoteroManager.getCollectionsByKey(user, groupId,
                keysToRetrieve);
        response.getCollections().forEach(c -> storeCitationCollection(c));
        return response.getContentVersion();
    }

    private void storeCitation(ICitation citation) {
        Optional<ICitation> optional = citationRepo.findByKey(citation.getKey());
        if (optional.isPresent()) {
            citationRepo.delete((Citation) optional.get());
        }
        citationRepo.save((Citation) citation);
    }

    private void storeCitationCollection(ICitationCollection collection) {
        Optional<ICitationCollection> optional = collectionRepo.findByKey(collection.getKey());
        if (optional.isPresent()) {
            collectionRepo.delete((CitationCollection) optional.get());
        }
        collectionRepo.save((CitationCollection) collection);
    }

}
