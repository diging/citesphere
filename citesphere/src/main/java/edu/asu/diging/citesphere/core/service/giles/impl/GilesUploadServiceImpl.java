package edu.asu.diging.citesphere.core.service.giles.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.core.service.giles.GilesUploadService;
import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class GilesUploadServiceImpl implements GilesUploadService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;
    
    @Autowired
    private InternalTokenManager internalTokenManager;
    
    @Autowired
    private IUserManager userManager;

    @Value("${giles_baseurl}")
    private String gilesBaseurl;

    @Value("${giles_upload_endpoint}")
    private String uploadEndpoint;
    
    @Value("${giles_check_endpoint}")
    private String gilesCheckEndpoint;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.giles.impl.GilesUploadService#
     * uploadFile(edu.asu.diging.citesphere.user.IUser,
     * org.springframework.web.multipart.MultipartFile, byte[])
     */
    @Override
    public IGilesUpload uploadFile(IUser user, MultipartFile file, byte[] fileBytes) {
        
        String token = getToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new MultipartFileResource(fileBytes, file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UploadResponse> response = restTemplate.postForEntity(gilesBaseurl + uploadEndpoint, requestEntity, UploadResponse.class);
        
        IGilesUpload upload = new GilesUpload();
        upload.setProgressId(response.getBody().getId());
        upload.setUploadingUser(user.getUsername());
        return upload;
    }
    
    private String getToken(IUser user) {
        OAuth2AccessToken token = internalTokenManager.getAccessToken(user);
        return token.getValue();
    }
    
    @Override
    public Set<IGilesUpload> getUploadId(String progressId, String userName) {
        Set<IGilesUpload> uploadSet = new HashSet<>();
        
        IUser user = userManager.findByUsername(userName);
        String token = getToken(user);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                headers);

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    gilesBaseurl + gilesCheckEndpoint + progressId,
                    HttpMethod.GET, requestEntity, String.class);
        } catch (HttpClientErrorException ex) {
            logger.error("Unable to get response for giles check");
            return null;
        }
        if (response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == HttpStatus.OK) {
            // Giles is still procoessing
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = response.getBody();
            GilesUpload[] uploads = new GilesUpload[0];
            try {
                uploads = mapper.readValue(jsonBody, GilesUpload[].class);
            } catch (IOException e) {
                logger.error("Could not deserialize response.", e);
                return null;
            }
            for (GilesUpload upload : uploads) {
                // giles does not return the progress id again, but we need it
                upload.setProgressId(progressId);
                uploadSet.add(upload);
            }
            return uploadSet;
        }
        return null; 
    }
    
    public class MultipartFileResource extends ByteArrayResource {

        private String filename;

        public MultipartFileResource(byte[] bytearray, String filename) {
            super(bytearray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
