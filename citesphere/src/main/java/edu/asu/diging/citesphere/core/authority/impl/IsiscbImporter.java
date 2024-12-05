package edu.asu.diging.citesphere.core.authority.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

@Component
@PropertySource(value = "classpath:/config.properties")
public class IsiscbImporter extends BaseAuthorityImporter {

    private final String ID = "authority.importer.isiscb";

    @Value("${_isiscb_authority_search_keyword}")
    private String isiscbSearchKeyword;

    @Value("${_isiscb_url}")
    private String isiscbURL;

    @Value("${_isiscb_uri_string}")
    private String isiscbUriString;
    
    @Value("${_isiscb_token}")
    private String isisCBtoken;

    private RestTemplate restTemplate;

    @PostConstruct
    private void postConstruct() {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);

        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.authority.impl.AuthorityImporter#isResponsible(java
     * .lang.String)
     */
    @Override
    public boolean isResponsible(String source) {
        if (source.equals(ISISCB)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isResponsibleForSearch(String source) {
        if (source.equals(ISISCB)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.authority.impl.AuthorityImporter#
     * retrieveAuthorityData(java.lang.String)
     */
    @Override
    public IImportedAuthority retrieveAuthorityData(String searchString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return ID;
    }

    /**
     * Searches for authorities in the ISISCB database based on the given parameters.
     *
     * @param firstName the first name of the authority to search for (can be null)
     * @param lastName  the last name of the authority to search for (can be null)
     * @param page      the current page number for pagination (0-based index)
     * @param pageSize  the number of results per page
     * @return an {@link AuthoritySearchResult} containing the list of found authorities 
     *         and pagination details
     * @throws AuthorityServiceConnectionException if there is a connection issue or 
     *         if the service returns an error status
     */
    @Override
    public AuthoritySearchResult searchAuthorities(String firstName, String lastName, int page, int pageSize)
            throws AuthorityServiceConnectionException {

        String url = isiscbURL + isiscbSearchKeyword + this.getIsiscbSearchString(firstName, lastName)
        + this.getLimitOffsetString(page, pageSize);

        HttpHeaders isisCbHeader = new HttpHeaders();
        isisCbHeader.set("Authorization", "Token " + isisCBtoken);

        HttpEntity<String> entityReq = new HttpEntity<String>(isisCbHeader);

        ResponseEntity<IsiscbResponse> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entityReq, IsiscbResponse.class);
        } catch (RestClientException ex) {
            throw new AuthorityServiceConnectionException(ex);
        }

        List<IAuthorityEntry> authorityEntries = new ArrayList<IAuthorityEntry>();
        AuthoritySearchResult searchResult = new AuthoritySearchResult();
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AuthorityServiceConnectionException("Error" + response.getStatusCode().toString() 
                    + " " + response.getBody().toString());
        }
        
        IsiscbResponse isiscbEntries = response.getBody();
        if (isiscbEntries.getResults() != null) {
            for (IsiscbEntry isiscbEntry : isiscbEntries.getResults()) {
                IAuthorityEntry authority = new AuthorityEntry();
                authority.setName(isiscbEntry.getName());
                authority.setId(isiscbEntry.getId());
                authority.setUri(isiscbUriString.replace("{0}", isiscbEntry.getId()));
                authority.setDescription(isiscbEntry.getDescription());
                authorityEntries.add(authority);
            }
            searchResult.setFoundAuthorities(authorityEntries);
            searchResult.setTotalPages((int) Math.ceil(isiscbEntries.getCount() / new Float(pageSize)));
        }

        return searchResult;
    }

    /**
     * Constructs a search string for ISISCB by combining the first and last name of the Author.
     *
     * @param firstName the first name of the individual (can be null)
     * @param lastName  the last name of the individual (can be null)
     * @return the constructed search string in the format "firstName+lastName", 
     *         or just the non-null value if one of the parameters is null.
     *         Returns {@code null} if both parameters are null.
     */
    private String getIsiscbSearchString(String firstName, String lastName) {
        String isisCBSearchString;
        if (firstName != null && lastName != null) {
            isisCBSearchString = firstName + "+" + lastName;
        } else {
            isisCBSearchString = firstName == null ? lastName : firstName;
        }
        return isisCBSearchString;
    }

    private String getLimitOffsetString(int page, int pageSize) {
        return "&limit=" + pageSize + "&offset=" + pageSize * page; 
    }

}
