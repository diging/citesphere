package edu.asu.diging.citesphere.core.util.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.IAffiliation;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.model.bib.impl.CitationConceptTag;
import edu.asu.diging.citesphere.model.bib.impl.ConceptType;
import edu.asu.diging.citesphere.model.bib.impl.Creator;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.user.IUser;
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
    public void updateCitation(ICitation citation, CitationForm form, IUser user) {
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

        citation.setAuthors(new HashSet<>());
        if (form.getAuthors() != null) {
            mapPersonFields(form.getAuthors(), citation.getAuthors());
        }

        citation.setEditors(new HashSet<>());
        if (form.getEditors() != null) {
            mapPersonFields(form.getEditors(), citation.getEditors());
        }

        citation.setOtherCreators(new HashSet<>());
        if (form.getOtherCreators() != null) {
            mapCreatorFields(form.getOtherCreators(), citation.getOtherCreators());
        }

        citation.setConceptTags(new HashSet<>());
        if (form.getConceptTags() != null) {
            for (ConceptAssignmentForm assignment : form.getConceptTags()) {
                if (assignment.getConceptId() != null && assignment.getConceptTypeId() != null) {
                    ICitationConceptTag tag = new CitationConceptTag();
                    ICitationConcept concept = conceptManager.getByUriAndOwner(assignment.getConceptUri(), user);
                    IConceptType type = typeManager.getByUriAndOwner(assignment.getConceptTypeUri(), user);
                    if (concept == null) {
                        concept = new CitationConcept();
                        concept.setName(assignment.getConceptName());
                        concept.setUri(assignment.getConceptUri());
                        concept.setOwner(user);
                        concept = conceptManager.save(concept);
                    }
                    if (type == null) {
                        type = new ConceptType();
                        type.setName(assignment.getConceptTypeName());
                        type.setUri(assignment.getConceptTypeUri());
                        type.setOwner(user);
                        type = typeManager.save(type);
                    }

                    tag.setConceptName(concept.getName());
                    tag.setConceptUri(concept.getUri());
                    tag.setLocalConceptId(concept.getId());
                    tag.setTypeName(type.getName());
                    tag.setTypeUri(type.getUri());
                    tag.setLocalConceptTypeId(type.getId());

                    citation.getConceptTags().add(tag);
                }
            }
        }
    }
    
    /**
     * Use updateCitationWithCollection(ICitation, String, IUser) method when moving
     * citation to a collection
     * 
     * @param citation     Citation that has to be updated with collection id.
     * @param collectionId collectionId is the id of collection that the citation is
     *                     moved to. Add collection id to citation 
     * @param iUser        user who is accessing Zotero.
     */
    @Override
    public void addCollection(ICitation citation, String collectionId, IUser iUser) {
        List<String> collections = Optional.ofNullable(citation.getCollections()).orElse(new ArrayList<String>());
        collections.add(collectionId);
        citation.setCollections(collections);
    }

    private void mapPersonFields(List<PersonForm> personList,
            Set<IPerson> citationPersonList) {
        for (PersonForm personForm : personList) {
            IPerson person = new Person();
            mapPersonFields(personForm, person);
            citationPersonList.add(person);
        }
    }

    private void mapPersonFields(PersonForm personForm, IPerson person) {

        person.setFirstName(personForm.getFirstName());
        person.setLastName(personForm.getLastName());
        person.setName(String.join(" ", personForm.getFirstName(), personForm.getLastName()));

        person.setAffiliations(new HashSet<>());
        if (personForm.getAffiliations() != null) {
            for (AffiliationForm affiliationForm : personForm.getAffiliations()) {
                IAffiliation affiliation = new Affiliation();
                affiliation.setName(affiliationForm.getName());
                person.getAffiliations().add(affiliation);
            }
        }
        person.setUri(personForm.getUri());
        person.setLocalAuthorityId(personForm.getLocalAuthorityId());
    }

    private void mapCreatorFields(List<PersonForm> personList,
            Set<ICreator> citationCreatorList) {
        for (PersonForm personForm : personList) {
            ICreator creator = new Creator();
            creator.setRole(personForm.getRole());
            IPerson person = new Person();
            creator.setPerson(person);
            
            mapPersonFields(personForm, creator.getPerson());
            citationCreatorList.add(creator);
        }
    }
}
