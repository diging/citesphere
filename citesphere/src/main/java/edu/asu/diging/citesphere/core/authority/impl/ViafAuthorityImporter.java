package edu.asu.diging.citesphere.core.authority.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
import edu.asu.diging.citesphere.core.authority.impl.ViafResponse.Data;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;

@Component
@PropertySource(value="classpath:/config.properties")
public class ViafAuthorityImporter extends BaseAuthorityImporter {
    
    private final String ID = "authority.importer.viaf";

    @Value("${_viaf_url_regex}")
    private String viafUrlRegex;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.AuthorityImporter#isResponsible(java.lang.String)
     */
    @Override
    public boolean isResponsible(String uri) {
        Pattern pattern = Pattern.compile(viafUrlRegex);
        Matcher matcher = pattern.matcher(uri);
        
        if (matcher.matches()) {
            return true;
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.AuthorityImporter#retrieveAuthorityData(java.lang.String)
     */
    @Override
    @Cacheable("viafAuthorities")
    public IImportedAuthority retrieveAuthorityData(String uri) throws URISyntaxException, AuthorityServiceConnectionException {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create()
                                                       .setRedirectStrategy(new LaxRedirectStrategy())
                                                       .build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        
        RequestEntity<Void> request = RequestEntity
                .get(new URI(uri))
                .accept(MediaType.APPLICATION_JSON).build();
        ResponseEntity<ViafResponse> response = null;
        try {
            response = restTemplate.exchange(request, ViafResponse.class);
        } catch (RestClientException ex) {
            throw new AuthorityServiceConnectionException(ex);
        }
        if (response.getStatusCode() == HttpStatus.OK) {
            ViafResponse viaf = response.getBody();
            Iterator<Data> iterator = viaf.getMainHeadings().getData().iterator();
            if (iterator.hasNext()) {
                // let's get the first entry for now
                String name = iterator.next().getText();
                ImportedAuthority authority = new ImportedAuthority();
                authority.setName(name);
                authority.setUri(viaf.getDocument().get("@about") + "");
                return authority;
            }
        }
        return null;
    }
    
    @Override
    public String getId() {
        return ID;
    }
}
