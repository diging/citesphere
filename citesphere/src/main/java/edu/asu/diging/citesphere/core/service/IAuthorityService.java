package edu.asu.diging.citesphere.core.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;

public interface IAuthorityService {

    void register(AuthorityImporter importer);

    IAuthorityEntry importAuthority(String uri)  throws AuthorityServiceConnectionException, URISyntaxException;

    IAuthorityEntry save(IAuthorityEntry entry);

    List<IAuthorityEntry> getAll(IUser user);

    boolean deleteAuthority(String id);

    IAuthorityEntry find(String id);

    List<IAuthorityEntry> findByUri(IUser user, String uri);

    Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId) throws GroupDoesNotExistException;

    IAuthorityEntry create(IAuthorityEntry entry, IUser user);
}