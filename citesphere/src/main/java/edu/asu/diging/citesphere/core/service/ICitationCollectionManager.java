package edu.asu.diging.citesphere.core.service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationCollectionResult;

public interface ICitationCollectionManager {

    CitationCollectionResult getTopCitationCollections(IUser user, String groupId, String parentCollectionId, int page,
            String sortBy) throws GroupDoesNotExistException;

}