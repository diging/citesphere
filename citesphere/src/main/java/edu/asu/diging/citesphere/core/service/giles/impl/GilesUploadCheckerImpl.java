package edu.asu.diging.citesphere.core.service.giles.impl;

import java.io.IOException;
import java.util.Arrays;
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
import org.springframework.dao.DuplicateKeyException;
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
    public void add(ICitation upload) {
        if (!uploadQueue.contains(upload.getKey())) {
            uploadQueue.add(upload.getKey());
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
            ICitation citation = citationManager.getCitation(citationKey);
            Set<IGilesUpload> checkedUploads = new HashSet<>();
            boolean needsUpdating = false;
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
                    needsUpdating = true;
                    continue;
                }
                if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                    // Giles is still procoessing
                    logger.debug("Upload " + upload.getProgressId()
                            + " still being processed.");
                    checkedUploads.add(upload);
                    continue;
                } else if (response.getStatusCode() == HttpStatus.OK) {
                    logger.debug("Upload " + upload.getProgressId() + " is done.");
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonBody = response.getBody();
                    GilesUpload[] processed = new GilesUpload[0];
                    try {
                        processed = mapper.readValue(jsonBody, GilesUpload[].class);
                    } catch (IOException e) {
                        logger.error("Could not deserialize response.", e);
                        upload.setDocumentStatus(GilesStatus.FAILED);
                        checkedUploads.add(upload);
                    }
                    for (GilesUpload processedUpload : processed) {
                        // giles does not return the progress id again, but we need it
                        processedUpload.setProgressId(upload.getProgressId());
                        checkedUploads.add(processedUpload);
                    }
                    needsUpdating = true;
                }
            }

            ICitation currentCitation = getCurrentCitation(citation, user);
            
            if (needsUpdating) {
                if (currentCitation != null) {
                    updateCitation(citation, checkedUploads, user, currentCitation);
                }
            }

            int unfinishedUplaods = currentCitation.getGilesUploads().stream()
                    .filter(u -> !Arrays.asList(GilesStatus.COMPLETE, GilesStatus.FAILED)
                            .contains(u.getDocumentStatus()))
                    .collect(Collectors.toList()).size();
            if (unfinishedUplaods == 0) {
                uploadQueue.remove(citation.getKey());
            }
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
                | ZoteroHttpStatusException | ZoteroItemCreationFailedException | DuplicateKeyException e) {
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
        } catch (DuplicateKeyException e) {
            logger.error("Duplicate key found.", e);
        }
        return null;
    }
}
