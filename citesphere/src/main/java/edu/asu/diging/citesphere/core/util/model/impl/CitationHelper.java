package edu.asu.diging.citesphere.core.util.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.model.bib.IAffiliation;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.core.model.bib.IConceptType;
import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationConceptTag;
import edu.asu.diging.citesphere.core.model.bib.impl.Creator;
import edu.asu.diging.citesphere.core.model.bib.impl.Person;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.web.forms.AffiliationForm;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.web.forms.ConceptAssignmentForm;
import edu.asu.diging.citesphere.web.forms.PersonForm;

@Component
public class CitationHelper implements ICitationHelper {
    
    @Autowired
    private ICitationConceptManager conceptManager;
    
    @Autowired
    private IConceptTypeManager typeManager;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.util.model.impl.ICitationHelper#updateCitation
     * (edu.asu.diging.citesphere.core.model.bib.ICitation,
     * edu.asu.diging.citesphere.web.forms.CitationForm)
     */
    @Override
    public void updateCitation(ICitation citation, CitationForm form) {
        citation.setAbstractNote(form.getAbstractNote());
        citation.setArchive(form.getArchive());
        citation.setArchiveLocation(form.getArchiveLocation());
        citation.setCallNumber(form.getCallNumber());
        citation.setDateFreetext(form.getDateFreetext());
        citation.setDoi(form.getDoi());
        citation.setIssn(form.getIssn());
        citation.setIssue(form.getIssue());
        citation.setItemType(form.getItemType());
        citation.setJournalAbbreviation(form.getJournalAbbreviation());
        citation.setLanguage(form.getLanguage());
        citation.setLibraryCatalog(form.getLibraryCatalog());
        citation.setPages(form.getPages());
        citation.setPublicationTitle(form.getPublicationTitle());
        citation.setRights(form.getRights());
        citation.setSeries(form.getSeries());
        citation.setSeriesText(form.getSeriesText());
        citation.setSeriesTitle(form.getSeriesTitle());
        citation.setShortTitle(form.getShortTitle());
        citation.setTitle(form.getTitle());
        citation.setUrl(form.getUrl());
        citation.setVolume(form.getVolume());
        
        Map<String, IPerson> authorMap = new HashMap<>();
        if(citation.getAuthors()!=null) {
            citation.getAuthors().forEach(a -> authorMap.put(a.getId(), a));
        }
        citation.setAuthors(new HashSet<>());
        if (form.getAuthors() != null) {
            mapPersonFields(authorMap, form.getAuthors(), citation.getAuthors());
        }
        
        Map<String, IPerson> editorMap = new HashMap<>();
        if(citation.getEditors()!=null) {
            citation.getEditors().forEach(a -> editorMap.put(a.getId(), a));
        }
        citation.setEditors(new HashSet<>());
        if (form.getEditors() != null) {
            mapPersonFields(editorMap, form.getEditors(), citation.getEditors());
        }
        
        Map<String, ICreator> creatorMap = new HashMap<>();
        if(citation.getOtherCreators()!=null) {
            citation.getOtherCreators().forEach(a -> creatorMap.put(a.getId(), a));
        }
        citation.setOtherCreators(new HashSet<>());
        if (form.getOtherCreators() != null) {
            mapCreatorFields(creatorMap, form.getOtherCreators(), citation.getOtherCreators());
        }

        citation.setConceptTags(new HashSet<>());
        if (form.getConceptAssignments() != null) {
            for (ConceptAssignmentForm assignment : form.getConceptAssignments()) {
                if (assignment.getConceptId() != null && assignment.getConceptTypeId() != null) {
                    ICitationConceptTag tag = new CitationConceptTag();
                    ICitationConcept concept = conceptManager.getByUri(assignment.getConceptUri());
                    IConceptType type = typeManager.getByUri(assignment.getConceptTypeUri());
                    if (concept != null && type != null) {
                        tag.setConceptName(concept.getName());
                        tag.setConceptUri(concept.getUri());
                        tag.setLocalConceptId(concept.getId());
                        tag.setTypeName(type.getName());
                        tag.setTypeUri(type.getUri());
                        tag.setLocalConceptTypeId(type.getId());
                        
                        citation.getConceptTags().add(tag);
                    }
                    //TODO: Save concept and tag by uri 
                }
            }
        }
    }

    private void mapPersonFields(Map<String, IPerson> personMap,
            List<PersonForm> personList, Set<IPerson> citationPersonList) {
            for (PersonForm personForm : personList) {
                IPerson person;
                if (personForm.getId() != null && !personForm.getId().isEmpty()) {
                    person = personMap.get(personForm.getId());
                } else {
                    person = new Person();
                }
                mapPersonFields(personForm, person);
                citationPersonList.add(person);
          }
     }
    
    private void mapPersonFields(PersonForm personForm, IPerson person) {
        
        person.setFirstName(personForm.getFirstName());
        person.setLastName(personForm.getLastName());
        person.setName(String.join(" ", personForm.getFirstName(), personForm.getLastName()));

        Map<String, IAffiliation> affiliationMap = new HashMap<>();
        if (person.getAffiliations() != null) {
            person.getAffiliations().forEach(a -> affiliationMap.put(a.getId(), a));
        }
        person.setAffiliations(new HashSet<>());
        if (personForm.getAffiliations() != null) {
            for (AffiliationForm affiliationForm : personForm.getAffiliations()) {
                IAffiliation affiliation;
                if (affiliationForm.getId() != null && !affiliationForm.getId().isEmpty()) {
                    affiliation = affiliationMap.get(affiliationForm.getId());
                } else {
                    affiliation = new Affiliation();
                }
                affiliation.setName(affiliationForm.getName());
                person.getAffiliations().add(affiliation);
            }
        }
        person.setUri(personForm.getUri());
        person.setLocalAuthorityId(personForm.getLocalAuthorityId());
    }
    
    private void mapCreatorFields(Map<String, ICreator> creatorMap, List<PersonForm> personList, Set<ICreator> citationCreatorList) {
        for (PersonForm personForm : personList) {
            ICreator creator;
            if (personForm.getId() != null && !personForm.getId().isEmpty()) {
                creator = creatorMap.get(personForm.getId());
            } else {
                creator = new Creator();
                creator.setRole(personForm.getRole());
                IPerson person = new Person();
                creator.setPerson(person);
            }
            mapPersonFields(personForm, creator.getPerson());
            citationCreatorList.add(creator);
        }
    }
    
}
