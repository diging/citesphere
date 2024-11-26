package edu.asu.diging.citesphere.core.service;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollectionResult;
import edu.asu.diging.citesphere.user.IUser;

public interface ICitationCollectionManager {

    CitationCollectionResult getCitationCollections(IUser user, String groupId, String parentCollectionId, int page,
            String sortBy) throws GroupDoesNotExistException;

    ICitationCollection getCollection(IUser user, String groupId, String collectionId);

    long getTotalCitationCollections(IUser user, String groupId, String parentCollectionId) throws GroupDoesNotExistException;

    List<ICitationCollection> getAllCollections(IUser user, String groupId, String parentCollectionId, String sortBy, int maxCollections)
            throws GroupDoesNotExistException;
    
    void deleteLocalGroupCollections(String groupId);
    
    ICitationCollection createCollection(IUser user, String groupId, String collectionName, String parentCollection) 
            throws GroupDoesNotExistException, ZoteroItemCreationFailedException, ZoteroConnectionException;

}