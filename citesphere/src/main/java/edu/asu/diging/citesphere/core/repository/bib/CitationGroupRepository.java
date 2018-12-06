package edu.asu.diging.citesphere.core.repository.bib;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;

public interface CitationGroupRepository extends PagingAndSortingRepository<CitationGroup, Long> {

    
}
