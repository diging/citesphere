package edu.asu.diging.citesphere.core.repository.bib;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;

@JaversSpringDataAuditable
public interface CitationRepository extends PagingAndSortingRepository<Citation, String> {

    List<ICitation> findByGroupAndAuthorsUri(ICitationGroup group, String uri);
    
    
}
