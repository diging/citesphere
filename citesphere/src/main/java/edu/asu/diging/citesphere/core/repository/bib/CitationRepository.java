package edu.asu.diging.citesphere.core.repository.bib;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.bib.impl.Citation;

public interface CitationRepository extends PagingAndSortingRepository<Citation, Long> {

}
