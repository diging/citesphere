package edu.asu.diging.citesphere.core.repository.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;

public interface AuthorityRepository extends PagingAndSortingRepository<AuthorityEntry, String> {

    public List<IAuthorityEntry> findByUsernameAndNameContainingAndNameContainingOrderByName(
            @Param("username") String username, @Param("firstName") String firstName,
            @Param("lastName") String lastName, Pageable paging);

    public List<IAuthorityEntry> findByUsernameAndNameContainingAndNameContainingOrderByName(
            @Param("username") String username, @Param("firstName") String firstName,
            @Param("lastName") String lastName);

    public long countByUsernameAndNameContainingAndNameContainingOrderByName(@Param("username") String username,
            @Param("firstName") String firstName, @Param("lastName") String lastName);
    
    @Query("select a from AuthorityEntry a where :group member of a.groups and a.name like :firstName% and a.name like %:lastName order by a.name")
    public Page<IAuthorityEntry> findByGroupAndFirstNameAndLastName(
            @Param("group") Long group, @Param("firstName") String firstName,
            @Param("lastName") String lastName, Pageable paging);

}
