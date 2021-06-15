package edu.asu.diging.citesphere.core.search.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.javers.common.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.ZoteroCreatorTypes;
import edu.asu.diging.citesphere.core.search.data.ReferenceRepository;
import edu.asu.diging.citesphere.core.search.model.impl.Affiliation;
import edu.asu.diging.citesphere.core.search.model.impl.Person;
import edu.asu.diging.citesphere.core.search.model.impl.Reference;
import edu.asu.diging.citesphere.core.search.model.impl.Tag;
import edu.asu.diging.citesphere.core.search.service.Indexer;
import edu.asu.diging.citesphere.model.bib.IAffiliation;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;

/**
 * Service class to index {@link ICitation}s in Elasticsearch.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class IndexerImpl implements Indexer {
    
    @Autowired
    private ReferenceRepository referenceRepo;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.search.service.impl.Indexer#indexCitation(edu.asu.diging.citesphere.model.bib.ICitation)
     */
    @Override
    public void indexCitation(ICitation citation) {
        Reference reference = new Reference();
        mapCitation(citation, reference);
        
        referenceRepo.save(reference);        
    }
    
    @Override
    public void deleteCitation(ICitation citation) {
        referenceRepo.deleteById(citation.getKey());
    }
    
    @Override
    public void deleteCitationByGroupId(String groupId) {
        referenceRepo.deleteByGroup(groupId);
        
    }
    private void mapCitation(ICitation citation, Reference reference) {
        reference.setAbstractNote(citation.getAbstractNote());
        reference.setAccessDate(citation.getAccessDate());
        reference.setArchive(citation.getArchive());
        reference.setArchiveLocation(citation.getArchiveLocation());
        reference.setCallNumber(citation.getCallNumber());
        reference.setCollections(citation.getCollections());
        reference.setConceptTagIds(citation.getConceptTagIds());
        reference.setDate(citation.getDate());
        reference.setDateAdded(citation.getDateAdded());
        reference.setDateFreetext(citation.getDateFreetext());
        reference.setDateModified(citation.getDateModified());
        reference.setDeleted(citation.getDeleted() > 0 ? true : false);
        reference.setDoi(citation.getDoi());
        reference.setGroup(citation.getGroup());
        reference.setIssn(citation.getIssn());
        reference.setIssue(citation.getIssue());
        reference.setItemType(citation.getItemType());
        reference.setJournalAbbreviation(citation.getJournalAbbreviation());
        reference.setKey(citation.getKey());
        reference.setLanguage(citation.getLanguage());
        reference.setLibraryCatalog(citation.getLibraryCatalog());
        reference.setPages(citation.getPages());
        reference.setPublicationTitle(citation.getPublicationTitle());
        reference.setRights(citation.getRights());
        reference.setSeries(citation.getSeries());
        reference.setSeriesText(citation.getSeriesText());
        reference.setSeriesTitle(citation.getSeriesTitle());
        reference.setShortTitle(citation.getShortTitle());
        reference.setTitle(citation.getTitle());
        reference.setUrl(citation.getUrl());
        reference.setVolume(citation.getVolume());
        
        reference.setCreators(new HashSet<>());
        mapPersons(reference.getCreators(), Optional.ofNullable(citation.getAuthors()).orElse(new HashSet<>()), ZoteroCreatorTypes.AUTHOR);
        mapPersons(reference.getCreators(), Optional.ofNullable(citation.getEditors()).orElse(new HashSet<>()), ZoteroCreatorTypes.EDITOR);
        mapOtherCreators(reference.getCreators(), Optional.ofNullable(citation.getOtherCreators()).orElse(new HashSet<>()));
        reference.setConceptTags(new HashSet<>());
        mapTags(reference.getConceptTags(), Optional.ofNullable(citation.getConceptTags()).orElse(new HashSet<>()));
    }
    
    private void mapTags(Set<Tag> referenceTags, Set<ICitationConceptTag> tags) {
        tags.forEach(t -> {
            Tag tag = new Tag();
            tag.setConceptName(t.getConceptName());
            tag.setConceptUri(t.getConceptUri());
            tag.setLocalConceptId(t.getLocalConceptId());
            tag.setLocalConceptTypeId(t.getLocalConceptTypeId());
            tag.setTypeName(t.getTypeName());
            tag.setTypeUri(t.getTypeUri());
            referenceTags.add(tag);
        });
    }
    
    private void mapPersons(Set<Person> referencePersons, Set<IPerson> persons, String type) {
        persons.forEach(p -> {
            Person person = new Person();
            person.setFirstName(p.getFirstName());
            person.setLastName(p.getLastName());
            person.setLocalAuthorityId(p.getLocalAuthorityId());
            person.setName(p.getName());
            person.setRole(type);
            person.setUri(p.getUri());
            person.setPositionInList(p.getPositionInList());
            person.setAffiliations(new HashSet<>());
            mapAffiliations(person.getAffiliations(), Optional.ofNullable(p.getAffiliations()).orElse(new HashSet<>()));
            referencePersons.add(person);
        });
    }
    
    private void mapOtherCreators(Set<Person> referencePersons, Set<ICreator> creators) {
        creators.forEach(c -> {
            mapPersons(referencePersons, Sets.asSet(c.getPerson()), c.getRole());
        });
    }
    
    private void mapAffiliations(Set<Affiliation> referenceAffiliations, Set<IAffiliation> affiliations) {
        affiliations.forEach(a -> {
            Affiliation affili = new Affiliation();
            affili.setLocalAuthorityId(a.getLocalAuthorityId());
            affili.setName(a.getName());
            affili.setUri(a.getUri());
            referenceAffiliations.add(affili);
        });
    }

}
