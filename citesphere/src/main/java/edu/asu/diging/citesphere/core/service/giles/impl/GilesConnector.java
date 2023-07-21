package edu.asu.diging.citesphere.core.service.giles.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.citesphere.core.service.giles.IGilesConnector;
import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.user.IUser;

@Component
@PropertySource({ "classpath:config.properties",
    "${appConfigFile:classpath:}/app.properties" })
public class GilesConnector implements IGilesConnector {

    @Value("${giles_baseurl}")
    private String gilesBaseurl;
    
    @Value("${giles_file_endpoint}")
    private String fileEndpoint;
    
    @Autowired
    private InternalTokenManager internalTokenManager;

    private RestTemplate restTemplate;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
    }
    

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.giles.impl.IGilesConnector#sendRequest(edu.asu.diging.citesphere.user.IUser, java.lang.String, java.lang.Class)
     */
    @Override
    public <T> ResponseEntity<T> sendRequest(IUser user, String endpoint,Class<T> returnType) throws HttpClientErrorException {
        String token = internalTokenManager.getAccessToken(user).getValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                headers);

        return restTemplate.exchange(
                    gilesBaseurl + endpoint,
                    HttpMethod.GET, requestEntity, returnType);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.giles.impl.IGilesConnector#getFile(edu.asu.diging.citesphere.user.IUser, java.lang.String)
     */
    @Override
    public byte[] getFile(IUser user, String fileId) {
        ResponseEntity<byte[]> content = sendRequest(user, fileEndpoint.replace("{0}",  fileId), byte[].class);
        return content.getBody();
    }
    
    @Override
    public void deleteDocument(String documentId) {
        
    }
}
