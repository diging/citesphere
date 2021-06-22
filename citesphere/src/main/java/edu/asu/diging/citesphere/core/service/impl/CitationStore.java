package edu.asu.diging.citesphere.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.search.service.Indexer;
import edu.asu.diging.citesphere.core.service.ICitationStore;
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
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationStore#save(edu.asu.diging.citesphere.model.bib.ICitation)
     */
    @Override
    public ICitation save(ICitation citation) {
        indexer.indexCitation(citation);
        return citationRepository.save((Citation) citation);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationStore#findById(java.lang.String)
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

    @Override
    public List<ICitation> getAttachments(String id) {
        return citationRepository.findByParentItemAndItemTypeAndDeleted(id, ItemType.ATTACHMENT.name(), 0);
    }
}
