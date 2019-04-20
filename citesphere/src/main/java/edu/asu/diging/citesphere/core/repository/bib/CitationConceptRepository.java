package edu.asu.diging.citesphere.core.repository.bib;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationConcept;

@JaversSpringDataAuditable
public interface CitationConceptRepository extends PagingAndSortingRepository<CitationConcept, String> {

    List<CitationConcept> findByOwner(IUser user);
}
