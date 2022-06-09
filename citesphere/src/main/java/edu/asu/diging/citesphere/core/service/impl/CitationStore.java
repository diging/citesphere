package edu.asu.diging.citesphere.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.search.service.Indexer;
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.core.sync.ExtraData;
import edu.asu.diging.citesphere.data.bib.CitationRepository;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.Citation;

/**
 * Service class to store and retrieve {@link ICitation}s.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class CitationStore implements ICitationStore {

    @Autowired
    private CitationRepository citationRepository;
    
    @Autowired 
    private Indexer indexer;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationStore#save(edu.asu.diging.citesphere.model.bib.ICitation)
     */
    @Override
    public ICitation save(ICitation citation) {
        indexer.indexCitation(citation);
        return citationRepository.save((Citation) citation);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationStore#findById(java.lang.String)
     */
    @Override
    public Optional<ICitation> findById(String id) {
        return citationRepository.findByKey(id);
    }
    
    @Override
    public void delete(ICitation citation) {
        indexer.deleteCitation(citation);
        citationRepository.delete((Citation) citation);
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationStore#getAttachments(java.lang.String)
     */
    @Override
    public List<ICitation> getAttachments(String id) {
        return citationRepository.findByParentItemAndItemTypeAndDeleted(id, ItemType.ATTACHMENT.name(), 0);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationStore#getNotes(java.lang.String)
     */
    @Override
    public List<ICitation> getNotes(String id) {
        return citationRepository.findByParentItemAndItemTypeAndDeletedAndTagsTagNot(id, ItemType.NOTE.name(), 0, ExtraData.CITESPHERE_METADATA_TAG);
    }

    @Override
    public void deleteCitationByGroupId(String groupId) {
        citationRepository.deleteByGroup(groupId);
        indexer.deleteCitationByGroupId(groupId);
    }
    
    @Override
    public List<ICitation> findByGilesDocumentId(String documentId) {
        return citationRepository.findByGilesUploadsDocumentId(documentId);
    }
}
