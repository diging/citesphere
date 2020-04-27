package edu.asu.diging.citesphere.core.authority.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

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
import org.springframework.web.util.UriComponentsBuilder;

import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.authority.impl.ViafResponse.Data;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

@Component
@PropertySource(value = "classpath:/config.properties")
public class ViafAuthorityImporter extends BaseAuthorityImporter {

    private final String ID = "authority.importer.viaf";

    @Value("${_viaf_url_regex}")
    private String viafUrlRegex;

    @Value("${_viaf_search_url}")
    private String viafsearchUrl;

    @Value("${_viaf_search_query}")
    private String viafsearchquery;

    @Value("${_viaf_page_size}")
    private String viafPageSizequery;

    @Value("${_viaf_record_start_index}")
    private String viafStartIndexQuery;

    @Value("${search_viaf_url_path2}")
    private String searchViafURLPath2;

    @Value("${_viaf_max_results}")
    private String viafMaxResults;

    @Value("${_viaf_max_page}")
    private int viafTotalPages;

    private RestTemplate restTemplate;

    @PostConstruct
    private void postConstruct() {
        restTemplate = new RestTemplate();
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
        Pattern pattern = Pattern.compile(viafUrlRegex);
        Matcher matcher = pattern.matcher(source);

        if (matcher.matches() || ID.contains(source)) {
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
    @Cacheable("viafAuthorities")
    public IImportedAuthority retrieveAuthorityData(String uri)
            throws URISyntaxException, AuthorityServiceConnectionException {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);

        RequestEntity<Void> request = RequestEntity.get(new URI(uri)).accept(MediaType.APPLICATION_JSON).build();
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

    @Override
    public AuthoritySearchResult searchAuthorities(String searchString, String source, int page, int pageSize)

            throws  AuthorityServiceConnectionException {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);

        String startIndex = page == 0 ? "1" : Integer.toString(page * pageSize);
        String fullUrl = "";
        RequestEntity<Void> request;
        try {
            fullUrl = viafsearchUrl.trim() + viafsearchquery.trim()
                    + URLEncoder.encode(searchString.trim(), StandardCharsets.UTF_8.toString())
                    + viafPageSizequery.trim() + viafMaxResults.trim() + viafStartIndexQuery.trim() + startIndex.trim()
                    + searchViafURLPath2.trim();
            URI uri = UriComponentsBuilder.fromUriString(fullUrl).build(true).toUri();
            request = RequestEntity.get(uri).accept(MediaType.APPLICATION_RSS_XML).build();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(StandardCharsets.UTF_8.toString() + " is not supported");
        }

        ResponseEntity<ViafReply> response = null;
        try {
            response = restTemplate.exchange(request, ViafReply.class);
        } catch (RestClientException ex) {
            throw new AuthorityServiceConnectionException(ex);
        }

        AuthoritySearchResult searchResult = new AuthoritySearchResult();
        if (response.getStatusCode() == HttpStatus.OK) {
            ViafReply rep = response.getBody();
            List<IAuthorityEntry> authorityEntries = new ArrayList<IAuthorityEntry>();
            List<Item> items = null;
            items = rep.getChannel().getItems();

            if (items != null) {
                for (Item i : items) {
                    IAuthorityEntry authority = new AuthorityEntry();
                    authority.setDescription(i.getTitle());
                    authority.setUri(i.getLink());
                    authority.setName(i.getLink());
                    authorityEntries.add(authority);
                }
            }
            searchResult.setFoundAuthorities(authorityEntries);
            searchResult.setTotalPages(viafTotalPages);
            searchResult.setCurrentPage(page + 1);
        } else {
            throw new AuthorityServiceConnectionException(response.getStatusCode().toString());
        }

        return searchResult;
    }

    @Override
    public boolean isResponsibleForSearch(String source) {
        // TODO Auto-generated method stub
        return false;
    }
}
