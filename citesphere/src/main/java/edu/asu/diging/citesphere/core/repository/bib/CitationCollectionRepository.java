package edu.asu.diging.citesphere.core.repository.bib;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationCollection;

@JaversSpringDataAuditable
public interface CitationCollectionRepository extends PagingAndSortingRepository<CitationCollection, String> {

    public CitationCollection findByKeyAndGroup(String id, ICitationGroup group);
    
    public List<ICitationCollection> findByParentCollectionKeyAndGroup(String parentCollectionKey, ICitationGroup group);
}
