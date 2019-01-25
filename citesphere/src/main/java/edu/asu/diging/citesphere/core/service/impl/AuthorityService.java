package edu.asu.diging.citesphere.core.service.impl;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import edu.asu.diging.citesphere.authority.AuthorityImporter;
import edu.asu.diging.citesphere.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.core.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.core.service.IAuthorityService;

@Service
public class AuthorityService implements IAuthorityService {

    private Set<AuthorityImporter> importers;
    
    @PostConstruct
    public void init() {
        importers = new HashSet<>();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IAuthorityService#register(edu.asu.diging.citesphere.authority.AuthorityImporter)
     */
    @Override
    public void register(AuthorityImporter importer) {
        this.importers.add(importer);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IAuthorityService#importAuthority(java.lang.String)
     */
    @Override
    public IAuthorityEntry importAuthority(String uri) throws URISyntaxException, AuthorityServiceConnectionException {
        for(AuthorityImporter importer : importers) {
            if (importer.isResponsible(uri)) {
                IImportedAuthority importedAuthority = importer.retrieveAuthorityData(uri);
                if (importedAuthority == null) {
                    return null;
                }
                
                IAuthorityEntry entry = new AuthorityEntry();
                entry.setName(importedAuthority.getName());
                entry.setUri(importedAuthority.getUri());
                return entry;
            }
        }
        return null;
    }
}
