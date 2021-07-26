package edu.asu.diging.citesphere.core.service;

import java.util.List;
import java.util.Optional;

import edu.asu.diging.citesphere.model.bib.ICitation;

public interface ICitationStore {

    /**
     * Method to save citations. This method will also index 
     * the citation before saving.
     * 
     * @param citation Citation to be saved.
     * @return the saved Citation
     */
    ICitation save(ICitation citation);

    Optional<ICitation> findById(String id);

    void delete(ICitation citation);

    void deleteCitationByGroupId(String groupId);
    
    List<ICitation> findByGilesDocumentId(String documentId);

}