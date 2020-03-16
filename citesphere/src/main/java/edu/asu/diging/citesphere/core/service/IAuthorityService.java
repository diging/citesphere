package edu.asu.diging.citesphere.core.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;

public interface IAuthorityService {

    void register(AuthorityImporter importer);

    IAuthorityEntry importAuthority(String uri)  throws AuthorityServiceConnectionException, URISyntaxException;
    
    List<IAuthorityEntry> importAuthorityEntries(String searchString, int page, int pageSize)  throws AuthorityServiceConnectionException, URISyntaxException;

    IAuthorityEntry save(IAuthorityEntry entry);

    List<IAuthorityEntry> getAll(IUser user);

    boolean deleteAuthority(String id);

    IAuthorityEntry find(String id);

    List<IAuthorityEntry> findByUri(IUser user, String uri);

    IAuthorityEntry create(IAuthorityEntry entry, IUser user);

    List<IAuthorityEntry> findByName(IUser user, String firstName, String lastName, int page, int pageSize);

    Set<IAuthorityEntry> findByNameInDataset(String firstName, String lastName, String citationGroupId,
            List<String> uris, int page, int pageSize) throws GroupDoesNotExistException;

    Set<IAuthorityEntry> findByNameInDataset(String firstName, String lastName, String citationGroupId, int page, int pageSize)
            throws GroupDoesNotExistException;

    Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId) throws GroupDoesNotExistException;

    int getTotalUserAuthorities(IUser user, String firstName, String lastName, int pageSize);

    int getTotalDatasetAuthorities(String citationGroupId, String firstName, String lastName, int pageSize);

    int getTotalImportedAuthorities(String conceptpowerSearchString, int pageSize);



}