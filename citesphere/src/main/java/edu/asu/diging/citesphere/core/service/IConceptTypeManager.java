package edu.asu.diging.citesphere.core.service;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.IConceptType;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

public interface IConceptTypeManager {

    IConceptType create(ConceptTypeForm form, IUser owner);

    List<IConceptType> getAllTypes(IUser owner);

    IConceptType get(String id);

    IConceptType getByUri(String conceptTypeUri);

}