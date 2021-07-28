package edu.asu.diging.citesphere.core.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.exceptions.AuthorityImporterNotFoundException;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

public interface IAuthorityService {

    void register(AuthorityImporter importer);

    IAuthorityEntry importAuthority(String uri) throws AuthorityServiceConnectionException, URISyntaxException;

    AuthoritySearchResult searchAuthorityEntries(IUser user, String firstName, String lastName, String source, int page,
            int pageSize) throws AuthorityServiceConnectionException, AuthorityImporterNotFoundException;

    IAuthorityEntry save(IAuthorityEntry entry);

    List<IAuthorityEntry> getAll(IUser user);

    boolean deleteAuthority(String id);

    IAuthorityEntry find(String id);

    List<IAuthorityEntry> findByUri(IUser user, String uri);

    IAuthorityEntry create(IAuthorityEntry entry, IUser user);

    /**
     * Finds the authorities whose name matches for the given first and last name
     * and which are created or imported by the given user
     * @param user      User who created or imported the authority
     * @param firstName First name to be searched for
     * @param lastName  Last name to be searched for
     * @param page      Requested page number
     * @param pageSize  Size of the requested page
     * @return Requested page of the found authorities
     */
    Page<IAuthorityEntry> findByFirstNameAndLastName(IUser user, String firstName, String lastName, int page, int pageSize);
    
    Page<IAuthorityEntry> findByLastNameAndExcludingFirstName(IUser user, String firstName, String lastName, int page, int pageSize);
    
    int getTotalUserAuthoritiesPages(IUser user, String firstName, String lastName, int pageSize);
    
    Page<IAuthorityEntry> findByGroupAndFirstNameAndLastName(Long groupId, String firstName, String lastName, int page, int pageSize);
    
    Page<IAuthorityEntry> findByGroupAndLastNameAndExcludingFirstName(Long groupId, String firstName, String lastName, int page, int pageSize);
    
    int getTotalGroupAuthoritiesPages(Long groupId, String firstName, String lastName, int pageSize);

    Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId) throws GroupDoesNotExistException;

    List<IAuthorityEntry> getAuthoritiesByGroup(long groupId);
        
    List<IAuthorityEntry> getUserSpecificAuthorities(IUser user);
}