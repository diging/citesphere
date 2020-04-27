package edu.asu.diging.citesphere.core.authority;

import java.net.URISyntaxException;

import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

/**
 * This interface needs to be implemented by all importer classes that provide
 * import of authority entries from an authority service.
 * 
 * Five methods need to be implemented:
 * <ul>
 * <li><code>isResponsible</code>: this method should return true if the
 * importer can import the provided URI (e.g. by check against a regex pattern)
 * </li>
 * <li><code>isResponsibleForSearch</code>: this method should return true if
 * the importer is responsible for search in the given source</li>
 * <li><code>retrieveAuthority</code>: this method should retrieve information
 * about the authority that belongs to the provided URI.</li>
 * <li><code>getId</code>: the id of this importer. There should be an entry in
 * config.properties under <code>_importer_name_[importerId]</code> for a human
 * readable name (e.g. VIAF for the VIAF authority service).</li>
 * <li><code>searchAuthorities</code>: this method should search authorities
 * from sources like viaf or conceptpower based on the first name and last name
 * of the authority</li>
 * </ul>
 * 
 * @author jdamerow
 *
 */
public interface AuthorityImporter {
    
    public static String CONCEPTPOWER="conceptpower";
    public static String VIAF="viaf";

    boolean isResponsible(String uri);

    boolean isResponsibleForSearch(String source);

    IImportedAuthority retrieveAuthorityData(String uri) throws URISyntaxException, AuthorityServiceConnectionException;

    AuthoritySearchResult searchAuthorities(String firstName, String lastName, int page, int pageSize)
            throws AuthorityServiceConnectionException;

    String getId();
}