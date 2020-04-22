package edu.asu.diging.citesphere.core.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

public interface IAuthorityService {

    void register(AuthorityImporter importer);

    IAuthorityEntry importAuthority(String uri) throws AuthorityServiceConnectionException, URISyntaxException;

    AuthoritySearchResult searchAuthorityEntries(IUser user, String firstName, String lastName, String source, int page,
            int pageSize) throws AuthorityServiceConnectionException, URISyntaxException;

    IAuthorityEntry save(IAuthorityEntry entry);

    List<IAuthorityEntry> getAll(IUser user);

    boolean deleteAuthority(String id);

    IAuthorityEntry find(String id);

    List<IAuthorityEntry> findByUri(IUser user, String uri);

    IAuthorityEntry create(IAuthorityEntry entry, IUser user);

    List<IAuthorityEntry> findByName(IUser user, String firstName, String lastName, int page, int pageSize);

    Set<IAuthorityEntry> findByNameInDataset(IUser user, String firstName, String lastName, String citationGroupId,
            int page, int pageSize) throws GroupDoesNotExistException;

    Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId) throws GroupDoesNotExistException;

    int getTotalUserAuthoritiesPages(IUser user, String firstName, String lastName, int pageSize);

    int getTotalDatasetAuthoritiesPages(String citationGroupId, String firstName, String lastName, int pageSize);

}