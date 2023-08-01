package edu.asu.diging.citesphere.core.service.giles.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.giles.IGilesConnector;
import edu.asu.diging.citesphere.core.service.giles.IGilesDeletionChecker;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Component
@PropertySource({ "classpath:config.properties",
    "${appConfigFile:classpath:}/app.properties" })
public class GilesDeletionChecker implements IGilesDeletionChecker {

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private IGilesConnector gilesConnector;

    @Value("${giles_document_deletion_check_endpoint}")
    private String gilesDeletionCheckEndpoint;
    
    private Queue<Map<String, String>> deletionQueue;
    
    @PostConstruct
    public void init() {
        deletionQueue = new ConcurrentLinkedQueue<Map<String, String>>();
    }
    
    @Override
    public void addDocumentCitationMap(IGilesUpload upload, ICitation citation, String zoteroId, IUser user) {
        HashMap<String, String> documentCitationMap = new HashMap<String, String>();
        documentCitationMap.put("documentId", upload.getDocumentId());
        documentCitationMap.put("citationKey", citation.getKey());
        documentCitationMap.put("userName", user.getUsername());
        documentCitationMap.put("zoteroId", zoteroId);
        System.out.println(documentCitationMap.get("zoteroId"));
        if (!deletionQueue.contains(documentCitationMap)) {
            deletionQueue.add(documentCitationMap);
        }
    }
    
    @Override
    @Scheduled(fixedDelay = 60000)
    public void checkDeletion() throws ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        for (Map<String, String> documentCitationMap: deletionQueue) {
            String documentId = documentCitationMap.get("documentId");
            String citationKey = documentCitationMap.get("citationKey");
            String zoteroId = documentCitationMap.get("zoteroId");
            ICitation citation = citationManager.getCitation(citationKey);
            IUser user = userManager.findByUsername(documentCitationMap.get("userName"));
            ResponseEntity<String> response = gilesConnector.sendRequest(user, gilesDeletionCheckEndpoint.replace("{documentId}",  documentId), String.class, HttpMethod.GET);
            if(response.getStatusCode().equals(HttpStatus.OK)) {
                for (Iterator<IGilesUpload> gileUpload = citation.getGilesUploads().iterator(); gileUpload.hasNext();) {
                    IGilesUpload g = gileUpload.next();
                    if (g.getDocumentId() != null && g.getDocumentId().equals(documentId)) {
                        gileUpload.remove();
                    }
                }
                System.out.println(zoteroId);
                citationManager.updateCitation(user, zoteroId, citation);
                deletionQueue.remove(documentCitationMap);
            }
        }
    }

}
