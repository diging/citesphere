package edu.asu.diging.citesphere.core.repository.bib;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.bib.impl.ConceptType;

@JaversSpringDataAuditable
public interface ConceptTypeRepository extends PagingAndSortingRepository<ConceptType, String> {

}
