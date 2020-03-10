package edu.asu.diging.citesphere.core.repository;

import java.util.Optional;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.user.IUser;

@JaversSpringDataAuditable
public interface CustomCitationConceptRepository extends PagingAndSortingRepository<CitationConcept, String> {
    
    Optional<CitationConcept> findFirstByUriAndOwner(String uri, IUser user);
}
