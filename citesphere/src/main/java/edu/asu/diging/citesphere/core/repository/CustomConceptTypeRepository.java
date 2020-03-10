package edu.asu.diging.citesphere.core.repository;

import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;
import edu.asu.diging.citesphere.model.bib.impl.ConceptType;
import edu.asu.diging.citesphere.user.IUser;

@JaversSpringDataAuditable
public interface CustomConceptTypeRepository extends PagingAndSortingRepository<ConceptType, String> {

    Optional<ConceptType> findFirstByUriAndOwner(String uri, IUser owner);
}
