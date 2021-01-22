package edu.asu.diging.citesphere.core.util.model;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;

public interface ICitationHelper {

    void updateCitation(ICitation citation, CitationForm form, IUser iUser);
    
    void updateCitation(ICitation citation, String collection, IUser iUser);
}