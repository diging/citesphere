package edu.asu.diging.citesphere.core.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationCollection;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationCollectionResult;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.repository.bib.CitationCollectionRepository;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
public class CitationCollectionManager implements ICitationCollectionManager {

    @Autowired
    private CitationCollectionRepository collectionRepository;
    
    @Autowired
    private CitationGroupRepository groupRepository;
    
    @Autowired
    private IZoteroManager zoteroManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationCollectionManager#getTopCitationCollections(edu.asu.diging.citesphere.core.model.IUser, java.lang.String, java.lang.String, int, java.lang.String)
     */
    @Override
    public CitationCollectionResult getTopCitationCollections(IUser user, String groupId, String parentCollectionId, int page, String sortBy) throws GroupDoesNotExistException {
        Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
        CitationCollectionResult collectionResult = new CitationCollectionResult();
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        
        CitationGroup group = groupOptional.get();
        List<ICitationCollection> collections = collectionRepository.findByParentCollectionKeyAndGroup(parentCollectionId, group);
        
        
        CitationCollectionResult results = zoteroManager.getTopCitationCollections(user, groupId, page, sortBy, group.getVersion());
        List<ICitationCollection> updatedCollections = results.getCitationCollections();
        if (!results.isNotModified()) {
            collectionRepository.deleteAll(Arrays.asList(collections.toArray(new CitationCollection[collections.size()])));
            collectionRepository.saveAll(Arrays.asList(updatedCollections.toArray(new CitationCollection[updatedCollections.size()])));
            collections = updatedCollections;
        } 
        
        collectionResult.setCitationCollections(collections);
        return results;
    }
}
