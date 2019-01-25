package edu.asu.diging.citesphere.core.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.authority.impl.AuthorityEntry;

@JaversSpringDataAuditable
public interface AuthorityEntryRepository extends PagingAndSortingRepository<AuthorityEntry, String> {

}
