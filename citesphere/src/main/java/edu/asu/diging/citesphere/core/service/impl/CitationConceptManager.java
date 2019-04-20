package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.core.repository.bib.CitationConceptRepository;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

@Service
public class CitationConceptManager implements ICitationConceptManager {

    @Autowired
    private CitationConceptRepository conceptRepo;
    
    @Autowired
    private IConceptTypeManager typeManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationConceptManager#findAll(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public List<ICitationConcept> findAll(IUser user) {
        List<ICitationConcept> concepts = new ArrayList<>();
        conceptRepo.findByOwner(user).forEach(c -> concepts.add(c));
        return concepts;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationConceptManager#create(edu.asu.diging.citesphere.web.forms.CitationConceptForm, edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public void create(CitationConceptForm conceptForm, IUser user) {
        ICitationConcept concept = new CitationConcept();
        concept.setName(conceptForm.getName());
        concept.setDescription(conceptForm.getDescription());
        concept.setType(typeManager.getById(conceptForm.getType()));
        concept.setUri(conceptForm.getUri());
        concept.setOwner(user);
        concept.setCreatedOn(OffsetDateTime.now());
        conceptRepo.save((CitationConcept)concept);
    }
}
