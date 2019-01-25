package edu.asu.diging.citesphere.core.service;

import java.net.URISyntaxException;

import edu.asu.diging.citesphere.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;

public interface IAuthorityService {

    void register(AuthorityImporter importer);

    IAuthorityEntry importAuthority(String uri)  throws AuthorityServiceConnectionException, URISyntaxException;

}