package edu.asu.diging.citesphere.core.service;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

public interface ICitationConceptManager {

    List<ICitationConcept> findAll(IUser user);

    void create(CitationConceptForm conceptForm, IUser user);

    ICitationConcept get(String conceptId);

    void save(ICitationConcept concept);

}