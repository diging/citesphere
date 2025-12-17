package edu.asu.diging.citesphere.core.service.giles.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.giles.GilesUploadChecker;
import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.GilesStatus;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Component
@PropertySource({ "classpath:config.properties",
    "${appConfigFile:classpath:}/app.properties" })
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

    private Queue<String> uploadQueue;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        uploadQueue = new ConcurrentLinkedQueue<String>();
        // load in progress uploads from db
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.giles.impl.GilesUploadChecker#add(
     * edu.asu.diging.citesphere.model.bib.ICitation)
     */
    @Override
    public void add(String uploadKey) {
        if (!uploadQueue.contains(uploadKey)) {
            uploadQueue.add(uploadKey);
        }
    }

    // in milliseconds (60000ms = 1m)
    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.giles.impl.GilesUploadChecker#
     * checkUploads()
     */
    @Override
    @Scheduled(fixedDelay = 60000)
    public void checkUploads() {
        for (String citationKey : uploadQueue) {
            checkUploadStatus(citationKey);
        }
    }
    
    /**
     * Checks the upload status of all Giles uploads associated with the given citation
     * and user. For each upload that is still in progress, it queries the Giles service
     * to retrieve its current status. Depending on the response, uploads may be marked
     * as COMPLETE, FAILED, or left in progress. If any uploads have updated statuses,
     * the citation is updated accordingly and, once all uploads are finished, the citation
     * is removed from the upload queue.
     *
     * @param citationKey the unique key identifying the citation whose uploads are being checked
     * @param user        the user owning the uploads; if null, the uploading user for each
     *                    upload is looked up via {@link IUserManager#findByUsername(String)}
     */
    @Override
    public void checkUploadStatus(String citationKey) {
        ICitation citation = citationManager.getCitation(citationKey);
        Set<IGilesUpload> checkedUploads = new HashSet<>();
        
        IUser user = null;
        
        for (IGilesUpload upload : citation.getGilesUploads()) {
            if (upload.getUploadingUser() == null
                    || Arrays.asList(GilesStatus.COMPLETE, GilesStatus.FAILED)
                            .contains(upload.getDocumentStatus())) {
                // in case something went wrong with the user
                // or the upload has been processed
                continue;
            }

            user = userManager.findByUsername(upload.getUploadingUser());
            checkedUploads.addAll(getFileStatus(upload, user));
        }

        ICitation currentCitation = getCurrentCitation(citation, user);
        
        if (currentCitation == null) {
            logger.error("Current Citation is null.");
            return;
        }
        
        updateCitation(citation, checkedUploads, user, currentCitation);
        int unfinishedUplaods = currentCitation.getGilesUploads().stream()
                .filter(u -> !Arrays.asList(GilesStatus.COMPLETE, GilesStatus.FAILED)
                        .contains(u.getDocumentStatus()))
                .collect(Collectors.toList()).size();
        if (unfinishedUplaods == 0) {
            uploadQueue.remove(citation.getKey());
        }
    }

    public void updateCitation(ICitation citation, Set<IGilesUpload> checkedUploads,
            IUser user, ICitation currentCitation) {
        for (IGilesUpload upload : checkedUploads) {
            Optional<IGilesUpload> oldUpload = currentCitation
                    .getGilesUploads().stream()
                    .filter(u -> u.getProgressId() != null && u
                            .getProgressId().equals(upload.getProgressId()))
                    .findFirst();
            if (oldUpload.isPresent()) {
                currentCitation.getGilesUploads().remove(oldUpload.get());
            }
            currentCitation.getGilesUploads().add(upload);
        }

        try {
            citationManager.updateCitation(user, citation.getGroup(),
                    currentCitation);
        } catch (ZoteroConnectionException | CitationIsOutdatedException
                | ZoteroHttpStatusException | ZoteroItemCreationFailedException e) {
            logger.error("Could not update citation.", e);
        }
    }

    public ICitation getCurrentCitation(ICitation citation, IUser user) {
        try {
            // we'll just use the last user here
            return citationManager.getCitation(user,
                    citation.getGroup(), citation.getKey());
        } catch (GroupDoesNotExistException e) {
            logger.error("Could not get citation.", e);
            uploadQueue.remove(citation.getKey());
        } catch (CannotFindCitationException e) {
            logger.error("Could not get citation.", e);
        } catch (ZoteroHttpStatusException e) {
            logger.error("Could not get citation.", e);
        }
        return null;
    }
    
    private Set<IGilesUpload> getFileStatus(IGilesUpload upload, IUser user) {
        Set<IGilesUpload> checkedUploads = new HashSet<>();        
        String token = internalTokenManager.getAccessToken(user).getValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    gilesBaseurl + gilesCheckEndpoint + upload.getProgressId(),
                    HttpMethod.GET, requestEntity, String.class);
        } catch (HttpClientErrorException ex) {
            upload.setDocumentStatus(GilesStatus.FAILED);
            checkedUploads.add(upload);
            return checkedUploads;
        }
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            // Giles is still procoessing
            logger.debug("Upload " + upload.getProgressId()
                    + " still being processed.");
            
            // Try to parse as GilesCheckUploadResponse to extract upload ID if available
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = response.getBody();
            try {
                GilesCheckUploadResponse checkResponse = mapper.readValue(jsonBody, GilesCheckUploadResponse.class);
                if (checkResponse.getUploadId() != null && !checkResponse.getUploadId().trim().isEmpty()) {
                    if (upload.getUploadId() == null || upload.getUploadId().trim().isEmpty()) {
                        upload.setUploadId(checkResponse.getUploadId());
                    }
                }
            } catch (IOException e) {
                logger.debug("Could not parse in-progress response as GilesCheckUploadResponse, continuing without upload ID extraction.", e);
            }
            
            checkedUploads.add(upload);
        } else if (response.getStatusCode() == HttpStatus.OK) {
            logger.debug("Upload " + upload.getProgressId() + " is done.");
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = response.getBody();
            try {
                GilesUpload[] processed = mapper.readValue(jsonBody, GilesUpload[].class);
                for (GilesUpload processedUpload : processed) {
                    // giles does not return the progress id again, but we need it
                    processedUpload.setProgressId(upload.getProgressId());
                    checkedUploads.add(processedUpload);
                }
            } catch (IOException e) {
                logger.error("Could not deserialize response.", e);
                upload.setDocumentStatus(GilesStatus.FAILED);
                checkedUploads.add(upload);
                return checkedUploads;
            }
        }
        return checkedUploads;
    }

    /**
     * Checks the upload status of a single file (identified by {@code processId})
     * associated with the given citation.
     *
     * @param citationKey the unique key identifying the citation whose file upload is being checked
     * @param user        the user who initiated the upload; used for authentication when querying status
     * @param processId   the progress ID of the specific file upload to check
     */
    @Override
    public void checkFileUploadStatus(String citationKey, IUser user, String processId) {
        ICitation citation = citationManager.getCitation(citationKey);
        Set<IGilesUpload> checkedUploads = new HashSet<>();
        if (citation == null || citation.getGilesUploads() == null) {
            return;
        }
        
        Optional<IGilesUpload> upload = Optional.ofNullable(citation)
                .map(ICitation::getGilesUploads)
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(u -> processId.equals(u.getProgressId()))
                .findFirst();
        
        if(upload.isPresent()) {
            if (Arrays.asList(GilesStatus.COMPLETE, GilesStatus.FAILED)
                            .contains(upload.get().getDocumentStatus())) {
                // in case something went wrong with the user
                // or the upload has been processed
                return;
            }
            checkedUploads.addAll(getFileStatus(upload.get(), user));
        } else {
            return;
        }
        
        ICitation currentCitation = getCurrentCitation(citation, user);
        
        if (currentCitation != null) {
            updateCitation(citation, checkedUploads, user, currentCitation);
        }        
    }
    
    /**
     * Check if an upload can be reprocessed based on Giles API v2 status
     * @param upload The upload to check
     * @param user The user making the request
     * @return true if upload can be reprocessed, false otherwise
     */
    public boolean canReprocess(IGilesUpload upload, IUser user) {
        if (upload.getProgressId() == null) {
            return false;
        }
        
        String token = internalTokenManager.getAccessToken(user).getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                gilesBaseurl + gilesCheckEndpoint + upload.getProgressId(),
                HttpMethod.GET, requestEntity, String.class);
                
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (HttpClientErrorException ex) {
            return ex.getStatusCode() == HttpStatus.NOT_FOUND || 
                   ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
