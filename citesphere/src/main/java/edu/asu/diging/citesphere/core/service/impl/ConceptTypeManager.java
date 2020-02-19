package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.repository.CustomConceptTypeRepository;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.data.bib.ConceptTypeRepository;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.model.bib.impl.ConceptType;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

@Service
public class ConceptTypeManager implements IConceptTypeManager {

	
    @Autowired
    private CustomConceptTypeRepository typeRepository;
    
    @Autowired
    private ConceptTypeRepository conceptTypeRepo;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IConceptTypeManager#create(edu.asu.diging.citesphere.web.forms.ConceptTypeForm, edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public IConceptType create(ConceptTypeForm form, IUser owner) {
        IConceptType type = new ConceptType();
        type.setName(form.getName());
        type.setDescription(form.getDescription());
        type.setUri(form.getUri());
        type.setOwner(owner);
        type.setCreatedOn(OffsetDateTime.now());
        
        save(type);
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
    public IConceptType get(String id) {
        Optional<ConceptType> type = conceptTypeRepo.findById(id);
        if (!type.isPresent()) {
            return null;
        }
        
        return type.get();
    }
    
    @Override
    public IConceptType getByUriAndOwner(String uri, IUser owner) {
        Optional<ConceptType> type = typeRepository.findFirstByUriAndOwner(uri, owner);
        if (!type.isPresent()) {
            return null;
        }
        return type.get();
    }
    @Override
    public IConceptType getByUri(String uri) {
        Optional<ConceptType> type = typeRepository.findByUri(uri);
        if (type.isPresent()) {
            return type.get();
        }
        return null;
    }
    
    
    @Override
    public IConceptType save(IConceptType type) {
        return typeRepository.save((ConceptType)type);
    }
}
