package edu.asu.diging.citesphere.core.repository;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;




@JaversSpringDataAuditable
public interface CustomCitationConceptRepository extends PagingAndSortingRepository<CitationConcept, String> {


    Optional<CitationConcept> findFirstByUriAndOwner(String uri, IUser user);
    Optional<CitationConcept> findByUri(String uri);
}
