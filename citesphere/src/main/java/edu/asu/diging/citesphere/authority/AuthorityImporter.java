package edu.asu.diging.citesphere.authority;

import java.net.URISyntaxException;

import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;

public interface AuthorityImporter {

    boolean isResponsible(String uri);

    IImportedAuthority retrieveAuthorityData(String uri)  throws URISyntaxException, AuthorityServiceConnectionException;

}