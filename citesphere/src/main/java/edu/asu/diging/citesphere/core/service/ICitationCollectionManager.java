package edu.asu.diging.citesphere.core.service;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollectionResult;

public interface ICitationCollectionManager {

    CitationCollectionResult getCitationCollections(IUser user, String groupId, String parentCollectionId, int page,
            String sortBy) throws GroupDoesNotExistException;

    ICitationCollection getCollection(IUser user, String groupId, String collectionId);

    long getTotalCitationCollections(IUser user, String groupId, String parentCollectionId) throws GroupDoesNotExistException;

    List<ICitationCollection> getAllCollections(IUser user, String groupId, String parentCollectionId, String sortBy, int maxCollections)
            throws GroupDoesNotExistException;

}