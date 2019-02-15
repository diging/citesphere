package edu.asu.diging.citesphere.core.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.core.model.authority.impl.AuthorityEntry;

@JaversSpringDataAuditable
public interface AuthorityEntryRepository extends PagingAndSortingRepository<AuthorityEntry, String> {

    public List<IAuthorityEntry> findByUsernameOrderByName(String username);
    
    public List<IAuthorityEntry> findByUsernameAndUriOrderByName(String username, String uri);
}
