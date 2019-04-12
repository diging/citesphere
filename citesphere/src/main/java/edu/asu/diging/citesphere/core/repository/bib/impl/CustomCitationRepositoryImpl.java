package edu.asu.diging.citesphere.core.repository.bib.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.repository.bib.CustomCitationRepository;

@Repository
public class CustomCitationRepositoryImpl implements CustomCitationRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detachCitation(ICitation citation) {
        entityManager.detach(citation);
    }
    
    @Override
    public ICitation mergeCitation(ICitation citation) {
        return entityManager.merge(citation);
    }

}
