package edu.asu.diging.citesphere.core.repository.bib;

import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.impl.ConceptType;

@JaversSpringDataAuditable
public interface ConceptTypeRepository extends PagingAndSortingRepository<ConceptType, String> {

    Optional<ConceptType> findFirstByUriAndOwner(String uri, IUser owner);

}
