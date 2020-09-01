package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.data.bib.CitationCollectionRepository;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.ICollectionMongoDao;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollection;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollectionResult;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource("classpath:/config.properties")
public class CitationCollectionManager implements ICitationCollectionManager {

    @Autowired
    private CitationCollectionRepository collectionRepository;
    
    @Autowired
    private ICollectionMongoDao collectionDao;
    
    @Autowired
    private CitationGroupRepository groupRepository;
    
    @Autowired
    private IZoteroManager zoteroManager;
    
    @Value("${_zotero_collections_max_number}")
    private Integer zoteroCollectionsMaxNumber;
    
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationCollectionManager#getTopCitationCollections(edu.asu.diging.citesphere.core.model.IUser, java.lang.String, java.lang.String, int, java.lang.String)
     */
    @Override
    public CitationCollectionResult getCitationCollections(IUser user, String groupId, String parentCollectionId, int page, String sortBy) throws GroupDoesNotExistException {
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        
        ICitationGroup group = groupOptional.get();
        List<ICitationCollection> collections = (List<ICitationCollection>) collectionDao.findByGroupIdAndParentKey(group.getGroupId() + "", parentCollectionId);
        CitationCollectionResult collectionResult = new CitationCollectionResult();
        collectionResult.setCitationCollections(collections);
        return collectionResult;
    }
    
    @Override
    public long getTotalCitationCollections(IUser user, String groupId, String parentCollectionId) throws GroupDoesNotExistException {
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        ICitationGroup group = groupOptional.get();
        
        CitationCollectionResult results = zoteroManager.getCitationCollections(user, groupId, parentCollectionId, 1, "title", group.getContentVersion());
        return results.getTotalResults();        
    }
    
    @Override
    public List<ICitationCollection> getAllCollections(IUser user, String groupId, String parentCollectionId, String sortBy, int maxCollections) throws GroupDoesNotExistException {
        int page = 1;
        CitationCollectionResult result = getCitationCollections(user, groupId, parentCollectionId, page, sortBy);
        List<ICitationCollection> collections = new ArrayList<>();
        if (result.getCitationCollections() != null) {
            collections.addAll(result.getCitationCollections());
        }
        int idx = 1;
        while (result.getTotalResults() > collections.size() && idx*zoteroCollectionsMaxNumber < maxCollections) {
            page++;
            idx++;
            result = getCitationCollections(user, groupId, parentCollectionId, page, sortBy);
            collections.addAll(result.getCitationCollections());
        }
        return collections;
    }
    
    @Override
    public ICitationCollection getCollection(IUser user, String groupId, String collectionId) {
        // zotero returns false if there is no parentcollection
        if (collectionId.equals("false")) {
            collectionId = null;
        }
        if (collectionId == null) {
            return null;
        }
        Optional<ICitationCollection> collectionOptional = collectionRepository.findByKey(collectionId);
        if (collectionOptional.isPresent()) {
            return (ICitationCollection) collectionOptional.get();
        }
        // FIXME: load collection asynch
        //ICitationCollection collection = zoteroManager.getCitationCollection(user, groupId, collectionId);
        //collectionRepository.save((CitationCollection)collection);
        return null;
        //return collection;
    }
}
