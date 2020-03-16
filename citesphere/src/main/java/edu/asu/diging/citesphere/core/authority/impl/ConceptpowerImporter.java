package edu.asu.diging.citesphere.core.authority.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;

@Component
@PropertySource(value = "classpath:/config.properties")
public class ConceptpowerImporter extends BaseAuthorityImporter {

    private final String ID = "authority.importer.conceptpower";

    @Value("${_conceptpower_authority_search_keyword}")
    private String conceptpowerSearchKeyword;

    @Value("${_conceptpower_url}")
    private String conceptpowerURL;
    
    @Value("${_viaf_url_regex}")
    private String viafUrlRegex;

    private RestTemplate restTemplate;
    
    private Pattern pattern;

    @PostConstruct
    private void postConstruct() {
        restTemplate = new RestTemplate();
        pattern = Pattern.compile(viafUrlRegex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.authority.impl.AuthorityImporter#isResponsible(java
     * .lang.String)
     */
    @Override
    public boolean isResponsible(String searchSyntax) {
        return searchSyntax.contains(conceptpowerSearchKeyword);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.authority.impl.AuthorityImporter#
     * retrieveAuthorityData(java.lang.String)
     */
    @Override
    public IImportedAuthority retrieveAuthorityData(String searchString)
            throws URISyntaxException, AuthorityServiceConnectionException {
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<IAuthorityEntry> retrieveAuthoritiesData(String searchString, int page, int pageSize)
            throws URISyntaxException, AuthorityServiceConnectionException {

        searchString = searchString.replace(conceptpowerSearchKeyword, "");
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);

        RequestEntity<Void> request;
        try {
            
            String url = conceptpowerURL + conceptpowerSearchKeyword + URLEncoder.encode(searchString, StandardCharsets.UTF_8.toString()) +"&page="+page;
            URI uri = UriComponentsBuilder.fromUriString(url.toString()).build(true).toUri();
            
            request = RequestEntity.get(uri)
                    .accept(MediaType.APPLICATION_JSON).build();
        } catch (UnsupportedEncodingException e) {
            throw new URISyntaxException(e.getMessage(),e.toString());
            
        }
        ResponseEntity<ConceptpowerResponse> response = null;
        try {
            response = restTemplate.exchange(request, ConceptpowerResponse.class);
        } catch (RestClientException ex) {
            throw new AuthorityServiceConnectionException(ex);
        }
        
        List<IAuthorityEntry> authorityEntries = new ArrayList<IAuthorityEntry>();
        if (response.getStatusCode() == HttpStatus.OK) {
            ConceptpowerResponse conceptEntries = response.getBody();
            if (conceptEntries.getConceptEntries() != null) {
                for (ConceptpowerEntry conceptEntry : conceptEntries.getConceptEntries()) {

                    IAuthorityEntry authority = new AuthorityEntry();
//                    if (conceptEntry.getEqual_to() == null || !pattern.matcher(conceptEntry.getEqual_to()).matches()) {
//                        continue;
//                    }
                    authority.setName(conceptEntry.getLemma());
                    authority.setUri(conceptEntry.getEqual_to());
                    authority.setDescription(conceptEntry.getDescription());
                    authorityEntries.add(authority);
                }
            }

        }
        return authorityEntries;
    }
    
    @Override
    public long totalRetrievedAuthorityData(String searchString)
            throws URISyntaxException, AuthorityServiceConnectionException {

        searchString = searchString.replace(conceptpowerSearchKeyword, "");
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);

        RequestEntity<Void> request;
        try {
            
            String url = conceptpowerURL + conceptpowerSearchKeyword + URLEncoder.encode(searchString, StandardCharsets.UTF_8.toString());
            URI uri = UriComponentsBuilder.fromUriString(url.toString()).build(true).toUri();
            
            request = RequestEntity.get(uri)
                    .accept(MediaType.APPLICATION_JSON).build();
        } catch (UnsupportedEncodingException e) {
            throw new URISyntaxException(e.getMessage(),e.toString());
            
        }
        ResponseEntity<ConceptpowerResponse> response = null;
        try {
            response = restTemplate.exchange(request, ConceptpowerResponse.class);
        } catch (RestClientException ex) {
            throw new AuthorityServiceConnectionException(ex);
        }
        
        if (response.getStatusCode() == HttpStatus.OK) {           
            ConceptpowerResponse conceptEntries = response.getBody();
            if (conceptEntries.getConceptEntries() != null) {              
               return conceptEntries.getPagination().getTotalNumberOfRecords();                               
            }           
        }
        
        return 0;
    }

}
