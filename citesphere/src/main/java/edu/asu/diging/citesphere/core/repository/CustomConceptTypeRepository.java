package edu.asu.diging.citesphere.core.repository;

import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.impl.ConceptType;



@JaversSpringDataAuditable
public interface CustomConceptTypeRepository extends PagingAndSortingRepository<ConceptType, String> {

    Optional<ConceptType> findFirstByUriAndOwner(String uri, IUser owner);

}
