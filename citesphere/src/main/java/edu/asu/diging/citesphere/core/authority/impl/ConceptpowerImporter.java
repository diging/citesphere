package edu.asu.diging.citesphere.core.authority.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;

@Component
@PropertySource(value="classpath:/config.properties")
public class ConceptpowerImporter extends BaseAuthorityImporter {

    private final String ID = "authority.importer.conceptpower";
    
    
    @Value("${_conceptpower_authority_search_keyword}")
    private String conceptpowerSearckKeyword;
    
    @Value("${_conceptpower_url}")
    private String conceptpowerURL;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.AuthorityImporter#isResponsible(java.lang.String)
     */
    @Override
    public boolean isResponsible(String searchSyntax) {
    	return searchSyntax.contains(conceptpowerSearckKeyword);
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.AuthorityImporter#retrieveAuthorityData(java.lang.String)
     */
    @Override
    public IImportedAuthority retrieveAuthorityData(String searchString) throws URISyntaxException, AuthorityServiceConnectionException {
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

	@Override
	public List<IAuthorityEntry> retrieveAuthoritiesData(String searchString) throws URISyntaxException, AuthorityServiceConnectionException {

        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create()
                                                       .setRedirectStrategy(new LaxRedirectStrategy())
                                                       .build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        
        RequestEntity<Void> request = RequestEntity
                .get(new URI(conceptpowerURL+searchString+"&Content-Type:application/json&Accept:application/json"))
                .accept(MediaType.APPLICATION_JSON).build();
        ResponseEntity<ConceptpowerResponse> response = null;
        try {
            response = restTemplate.exchange(request, ConceptpowerResponse.class);
        } catch (RestClientException ex) {
            throw new AuthorityServiceConnectionException(ex);
        }
        List<IAuthorityEntry> authorityEntries = new ArrayList<IAuthorityEntry>();
        if (response.getStatusCode() == HttpStatus.OK) {
        	ConceptpowerResponse conceptEntries = response.getBody();

            for(ConceptpowerEntry conceptEntry : conceptEntries.getConceptEntries()) {

            	IAuthorityEntry authority = new AuthorityEntry();
                if(conceptEntry.getEqual_to()==null || conceptEntry.getEqual_to()=="") {
                	continue;
                }
                authority.setName(conceptEntry.getLemma());
                authority.setUri(conceptEntry.getEqual_to());
                authority.setDescription(conceptEntry.getDescription());
                authorityEntries.add(authority);
            }
        }
        return authorityEntries;
	}

}
