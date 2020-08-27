package edu.asu.diging.citesphere.core.service;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

public interface IAsyncCitationProcessor {

    void loadCitations(IUser user, ICitationGroup group, String collectionId, String sortBy);

    
}