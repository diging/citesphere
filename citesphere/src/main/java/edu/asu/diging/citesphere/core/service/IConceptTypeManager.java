package edu.asu.diging.citesphere.core.service;

import java.util.List;
import java.util.Optional;

import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

public interface IConceptTypeManager {

    IConceptType create(ConceptTypeForm form, IUser owner);

    List<IConceptType> getAllTypes(IUser owner);

    IConceptType get(String id);

    IConceptType getByUriAndOwner(String conceptTypeUri, IUser owner);

    IConceptType save(IConceptType type);
    
    IConceptType getByUri(String uri);
     
    

}