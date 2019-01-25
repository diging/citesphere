package edu.asu.diging.citesphere.core.util.model;

import java.util.List;
import java.util.Map;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.web.forms.PersonForm;

public interface ICitationHelper {

    void updateCitation(ICitation citation, CitationForm form);

    void mapPersonFields(ICitation citation, Map<String, IPerson> personMap,
            List<PersonForm> personList, String personType);

}