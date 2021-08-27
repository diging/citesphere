package edu.asu.diging.citesphere.core.repository.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;

public interface AuthorityRepository extends PagingAndSortingRepository<AuthorityEntry, String> {

    public Page<IAuthorityEntry> findByUsernameAndNameContainingAndNameContainingOrderByName(
            @Param("username") String username, @Param("firstName") String firstName,
            @Param("lastName") String lastName, Pageable paging);

    public List<IAuthorityEntry> findByUsernameAndNameContainingAndNameContainingOrderByName(
            @Param("username") String username, @Param("firstName") String firstName,
            @Param("lastName") String lastName);
    
    public Page<IAuthorityEntry> findByUsernameAndNameNotContainingAndNameContainingOrderByName(
            @Param("username") String username, @Param("firstName") String firstName,
            @Param("lastName") String lastName, Pageable paging);

    public long countByUsernameAndNameContaining(@Param("username") String username, @Param("name") String name);

    public Page<IAuthorityEntry> findByGroupsContainingAndNameContainingAndNameContainingOrderByName(
            @Param("group") Long group, @Param("firstName") String firstName, @Param("lastName") String lastName,
            Pageable paging);
    
    public Page<IAuthorityEntry> findByGroupsContainingAndNameNotContainingAndNameContainingOrderByName(
            @Param("group") Long group, @Param("firstName") String firstName, @Param("lastName") String lastName,
            Pageable paging);

    public long countByGroupsContainingAndNameContaining(@Param("group") Long group, @Param("name") String name);
}
