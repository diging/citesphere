package edu.asu.diging.citesphere.core.service.giles.impl;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.service.giles.GilesUploadService;
import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource("classpath:/config.properties")
public class GilesUploadServiceImpl implements GilesUploadService {

    private RestTemplate restTemplate;
    
    @Autowired
    private InternalTokenManager internalTokenManager;

    @Value("${giles_baseurl}")
    private String gilesBaseurl;

    @Value("${giles_upload_endpoint}")
    private String uploadEndpoint;

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
        
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", fileBytes);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UploadResponse> response = restTemplate.postForEntity(gilesBaseurl + uploadEndpoint, requestEntity, UploadResponse.class);
        
        IGilesUpload upload = new GilesUpload();
        upload.setProgressId(response.getBody().getId());
        return upload;
    }
    
    private String getToken() {
        OAuth2AccessToken token = internalTokenManager.getAccessToken();
        return token.getValue();
    }

    class UploadResponse {
        private String id;
        private String checkUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCheckUrl() {
            return checkUrl;
        }

        public void setCheckUrl(String checkUrl) {
            this.checkUrl = checkUrl;
        }
    }
}
