package edu.asu.diging.citesphere.core.authority;

import java.net.URISyntaxException;
import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;

/**
 * This interface needs to be implemented by all importer classes that provide
 * import of authority entries from an authority service.
 * 
 * Three methods need to be implemented:
 * <ul>
 * <li><code>isResponsible</code>: this method should return true if the importer can
 * import the provided URI (e.g. by check against a regex pattern).</li>
 * <li><code>retrieveAuthority</code>: this method should retrieve information about the
 * authority that belongs to the provided URI.</li>
 * <li><code>getId</code>: the id of this importer. There should be an entry in 
 * config.properties under <code>_importer_name_[importerId]</code> for a human 
 * readable name (e.g. VIAF for the VIAF authority service).
 * </ul>
 * 
 * @author jdamerow
 *
 */
public interface AuthorityImporter {
    
    public static String CONCEPTPOWER="conceptpower";
    public static String VIAF="viaf";

    boolean isResponsible(String uri);

    IImportedAuthority retrieveAuthorityData(String uri) throws URISyntaxException, AuthorityServiceConnectionException;
    
    List<IAuthorityEntry> retrieveAuthoritiesData(String searchString, int page, int pageSize) throws URISyntaxException, AuthorityServiceConnectionException;

    String getId();

    long totalRetrievedAuthorityData(String searchString)
            throws URISyntaxException, AuthorityServiceConnectionException;

}