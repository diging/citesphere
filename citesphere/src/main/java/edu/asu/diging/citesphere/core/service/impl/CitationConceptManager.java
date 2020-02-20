package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.repository.CustomCitationConceptRepository;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.data.bib.CitationConceptRepository;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

@Service
public class CitationConceptManager implements ICitationConceptManager {

    @Autowired
    private CitationConceptRepository conceptRepo;
    
    @Autowired
    private CustomCitationConceptRepository customConceptRepo;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationConceptManager#findAll(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public List<ICitationConcept> findAll(IUser user) {
        List<ICitationConcept> concepts = new ArrayList<>();
        conceptRepo.findByOwner(user).forEach(c -> concepts.add(c));
        return concepts;
    }
    
    @Override
    public ICitationConcept get(String conceptId) {
        Optional<CitationConcept> concept = conceptRepo.findById(conceptId);
        if (concept.isPresent()) {
            return concept.get();
        }
        return null;
    }
    
    @Override
    public ICitationConcept getByUriAndOwner(String uri, IUser owner) {
        Optional<CitationConcept> concept = customConceptRepo.findFirstByUriAndOwner(uri, owner);
        if (concept.isPresent()) {
            return concept.get();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationConceptManager#create(edu.asu.diging.citesphere.web.forms.CitationConceptForm, edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public void create(CitationConceptForm conceptForm, IUser user) {
        ICitationConcept concept = new CitationConcept();
        concept.setName(conceptForm.getName());
        concept.setDescription(conceptForm.getDescription());
        concept.setUri(conceptForm.getUri());
        concept.setOwner(user);
        concept.setCreatedOn(OffsetDateTime.now());
        save(concept);
    }
    
    @Override
    public ICitationConcept save(ICitationConcept concept) {
        return conceptRepo.save((CitationConcept)concept);
    }
}
