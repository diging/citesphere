package edu.asu.diging.citesphere.core.service.giles.impl;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.giles.GilesUploadChecker;
import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class GilesUploadCheckerImpl implements GilesUploadChecker {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
        
    @Autowired
    private InternalTokenManager internalTokenManager;
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ICitationManager citationManager;

    @Value("${giles_baseurl}")
    private String gilesBaseurl;
    
    @Value("${giles_check_endpoint}")
    private String gilesCheckEndpoint;

    private RestTemplate restTemplate;

    private Queue<ICitation> uploadQueue;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        uploadQueue = new ConcurrentLinkedQueue<ICitation>();
        // load in progress uploads from db
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.giles.impl.GilesUploadChecker#add(edu.asu.diging.citesphere.model.bib.ICitation)
     */
    @Override
    public void add(ICitation upload) {
        if (!uploadQueue.contains(upload)) {
            uploadQueue.add(upload);
        }
    }
    
    // in milliseconds (60000ms = 1m)
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.giles.impl.GilesUploadChecker#checkUploads()
     */
    @Override
    @Scheduled(fixedDelay = 60000)
    public void checkUploads() {
        for (ICitation citation : uploadQueue) {
            Set<IGilesUpload> checkedUploads = new HashSet<>();
            boolean needsUpdating = false;
            IUser user = null;
            for (IGilesUpload upload : citation.getGilesUploads()) {
                if (upload.getUploadingUser() == null) {
                    // in case something went wrong with the user
                    continue;
                }
                user = userManager.findByUsername(upload.getUploadingUser());
                String token = internalTokenManager.getAccessToken(user).getValue();
    
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
    
                // FIXME: does giles expect upload id? if so, need to get that.
                ResponseEntity<String> response = restTemplate.exchange(gilesBaseurl + gilesCheckEndpoint + upload.getProgressId(), HttpMethod.GET, requestEntity, String.class);
                if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                    // Giles is still procoessing
                    logger.debug("Upload " + upload.getProgressId() + " still being processed.");
                    checkedUploads.add(upload);
                    continue;
                } else if (response.getStatusCode() == HttpStatus.OK) {
                    logger.debug("Upload " + upload.getProgressId() + " is done.");
                    Gson gson = new Gson();
                    GilesUpload processed = gson.fromJson(response.getBody(), GilesUpload.class);
                    checkedUploads.add(processed);
                    needsUpdating = true;
                }
            }
            if (needsUpdating) {
                citation.setGilesUploads(checkedUploads);
                // we'll just use the last user here
                try {
                    citationManager.updateCitation(user, citation.getGroup(), citation);
                } catch (ZoteroConnectionException | CitationIsOutdatedException | ZoteroHttpStatusException e) {
                    logger.error("Could not update citation.", e);
                }
            }
        }
    }
}
