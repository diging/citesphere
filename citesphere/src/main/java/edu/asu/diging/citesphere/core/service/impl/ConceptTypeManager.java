package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.IConceptType;
import edu.asu.diging.citesphere.core.model.bib.impl.ConceptType;
import edu.asu.diging.citesphere.core.repository.bib.ConceptTypeRepository;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

@Service
public class ConceptTypeManager implements IConceptTypeManager {

    @Autowired
    private ConceptTypeRepository typeRepository;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IConceptTypeManager#create(edu.asu.diging.citesphere.web.forms.ConceptTypeForm, edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public IConceptType create(ConceptTypeForm form, IUser owner) {
        ConceptType type = new ConceptType();
        type.setName(form.getName());
        type.setDescription(form.getDescription());
        type.setUri(form.getUri());
        type.setOwner(owner);
        type.setCreatedOn(OffsetDateTime.now());
        
        typeRepository.save(type);
        return type;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IConceptTypeManager#getAllTypes(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public List<IConceptType> getAllTypes(IUser owner) {
        List<IConceptType> types = new ArrayList<>();
        typeRepository.findAll().forEach(t -> types.add(t));
        return types;
    }
    
    @Override
    public IConceptType getById(String id) {
        Optional<ConceptType> type = typeRepository.findById(id);
        if (!type.isPresent()) {
            return null;
        }
        
        return type.get();
    }
}
