package edu.asu.diging.citesphere.core.repository.bib;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;

@JaversSpringDataAuditable
public interface CitationGroupRepository extends PagingAndSortingRepository<CitationGroup, Long> {

    
}
