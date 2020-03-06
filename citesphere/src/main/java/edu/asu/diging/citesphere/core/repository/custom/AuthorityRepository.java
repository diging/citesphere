package edu.asu.diging.citesphere.core.repository.custom;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;

public interface AuthorityRepository extends PagingAndSortingRepository<AuthorityEntry, String>{
	
    public List<IAuthorityEntry> findByUsernameAndNameContainingOrderByName(@Param("username") String username, @Param("name") String name);
	
}
