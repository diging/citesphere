package edu.asu.diging.citesphere.core.search.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;

import edu.asu.diging.citesphere.core.model.ZoteroCreatorTypes;
import edu.asu.diging.citesphere.core.search.model.impl.Affiliation;
import edu.asu.diging.citesphere.core.search.model.impl.Person;
import edu.asu.diging.citesphere.core.search.model.impl.Reference;
import edu.asu.diging.citesphere.core.search.model.impl.Tag;
import edu.asu.diging.citesphere.core.search.service.SearchEngine;
import edu.asu.diging.citesphere.core.service.impl.CitationPage;
import edu.asu.diging.citesphere.model.bib.IAffiliation;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationConceptTag;
import edu.asu.diging.citesphere.model.bib.impl.Creator;

/**
 * Service class to query Elasticsearch for references.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class SearchEngineImpl implements SearchEngine {

    @Autowired
    private ElasticsearchRestTemplate template;

    /**
     * {@inheritDoc}
     * 
     * This method searches in the title and creators name field for references that are
     * not deleted.
     * 
     */
    @Override
    public ResultPage search(String searchTerm, String groupId, int page, int pageSize) {
        BoolQueryBuilder orFieldsBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.queryStringQuery(searchTerm).field("title").field("creators.name"));
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(orFieldsBuilder).must(QueryBuilders.matchQuery("deleted", false)).must(QueryBuilders.matchQuery("group", groupId));
        NativeSearchQueryBuilder b = new NativeSearchQueryBuilder().withQuery(boolBuilder).withPageable(PageRequest.of(page, pageSize));

        AggregatedPage<Reference> results = template.queryForPage(b.build(), Reference.class);
        List<ICitation> foundCitations = new ArrayList<ICitation>();
        results.get().forEach(r -> {
            foundCitations.add(mapReference(r));
        });
        return new ResultPage(foundCitations, results.getTotalElements(), results.getTotalPages());
    }

    @Override
    public CitationPage getPrevAndNextCitation(String searchTerm, String groupId, int page, int index, int pageSize) {
        ResultPage searchResult = search(searchTerm, groupId, page, pageSize);
        List<ICitation> citations = searchResult.getResults();

        CitationPage citationPage = new CitationPage();
        citationPage.setIndex(String.valueOf(index));
        citationPage.setZoteroGroupId(groupId);

        if (citations != null && citations.size() > 0) {
            if (index == citations.size() - 1 && page < searchResult.getTotalPages() - 1) {
                ResultPage nextPageSearchResult = search(searchTerm, groupId, page + 1, pageSize);
                citationPage.setNext(nextPageSearchResult.getResults().get(0).getKey());
                citationPage.setNextIndex(String.valueOf(0));
                citationPage.setNextPage(String.valueOf(page + 2));
            } else if (index < citations.size() - 1) {
                citationPage.setNext(citations.get(index + 1).getKey());
                citationPage.setNextIndex(String.valueOf(index + 1));
                citationPage.setNextPage(String.valueOf(page + 1));
            }

            if (index > 0) {
                citationPage.setPrev(citations.get(index - 1).getKey());
                citationPage.setPrevIndex(String.valueOf(index - 1));
                citationPage.setPrevPage(String.valueOf(page + 1));
            } else if (index == 0 && page > 0) {
                ResultPage prevPageSearchResult = search(searchTerm, groupId, page - 1, pageSize);
                int prevPageSize = prevPageSearchResult.getResults().size();
                citationPage.setPrev(prevPageSearchResult.getResults().get(prevPageSize - 1).getKey());
                citationPage.setPrevIndex(String.valueOf(prevPageSize - 1));
                citationPage.setPrevPage(String.valueOf(page));
            }
        }

        return citationPage;
    }

    private ICitation mapReference(Reference ref) {
        ICitation citation = new Citation();
        citation.setAbstractNote(ref.getAbstractNote());
        citation.setAccessDate(ref.getAccessDate());
        citation.setArchive(ref.getArchive());
        citation.setArchiveLocation(ref.getArchiveLocation());
        citation.setCallNumber(ref.getCallNumber());
        citation.setCollections(ref.getCollections());
        citation.setConceptTagIds(ref.getConceptTagIds());
        citation.setDate(ref.getDate());
        citation.setDateAdded(ref.getDateAdded());
        citation.setDateFreetext(ref.getDateFreetext());
        citation.setDateModified(ref.getDateModified());
        citation.setDeleted(ref.getDeleted() ? 1 : 0);
        citation.setDoi(ref.getDoi());
        citation.setGroup(ref.getGroup());
        citation.setIssn(ref.getIssn());
        citation.setIssue(ref.getIssue());
        citation.setItemType(ref.getItemType());
        citation.setJournalAbbreviation(ref.getJournalAbbreviation());
        citation.setKey(ref.getKey());
        citation.setLanguage(ref.getLanguage());
        citation.setLibraryCatalog(ref.getLibraryCatalog());
        citation.setPages(ref.getPages());
        citation.setPublicationTitle(ref.getPublicationTitle());
        citation.setRights(ref.getRights());
        citation.setSeries(ref.getSeries());
        citation.setSeriesText(ref.getSeriesText());
        citation.setSeriesTitle(ref.getSeriesTitle());
        citation.setShortTitle(ref.getShortTitle());
        citation.setTitle(ref.getTitle());
        citation.setUrl(ref.getUrl());
        citation.setVolume(ref.getVolume());

        mapPersons(ref, citation);
        mapConceptTags(ref, citation);

        return citation;
    }

    private void mapPersons(Reference ref, ICitation citation) {
        citation.setAuthors(new HashSet<>());
        citation.setEditors(new HashSet<>());
        citation.setOtherCreators(new HashSet<>());
        for (Person person : Optional.fromNullable(ref.getCreators()).or(new HashSet<>())) {
            IPerson citPerson = new edu.asu.diging.citesphere.model.bib.impl.Person();
            mapPerson(person, citPerson);
            if (person.getRole().equals(ZoteroCreatorTypes.AUTHOR)) {
                citation.getAuthors().add(citPerson);
            } else if (person.getRole().equals(ZoteroCreatorTypes.EDITOR)) {
                citation.getEditors().add(citPerson);
            } else {
                ICreator creator = new Creator();
                creator.setRole(person.getRole());
                creator.setPerson(citPerson);
                citation.getOtherCreators().add(creator);
            }
        }
    }

    private void mapPerson(Person person, IPerson citPerson) {
        citPerson.setFirstName(person.getFirstName());
        citPerson.setLastName(person.getLastName());
        citPerson.setLocalAuthorityId(person.getLocalAuthorityId());
        citPerson.setName(person.getName());
        citPerson.setUri(person.getName());
        citPerson.setAffiliations(new HashSet<>());
        for (Affiliation aff : Optional.fromNullable(person.getAffiliations()).or(new HashSet<>())) {
            IAffiliation citAffiliation = new edu.asu.diging.citesphere.model.bib.impl.Affiliation();
            mapAffiliation(aff, citAffiliation);
            citPerson.getAffiliations().add(citAffiliation);
        }
    }

    private void mapAffiliation(Affiliation aff, IAffiliation citAffiliation) {
        citAffiliation.setLocalAuthorityId(aff.getLocalAuthorityId());
        citAffiliation.setName(aff.getName());
        citAffiliation.setUri(aff.getUri());
    }

    private void mapConceptTags(Reference ref, ICitation citation) {
        citation.setConceptTags(new HashSet<>());
        for (Tag tag : Optional.fromNullable(ref.getConceptTags()).or(new HashSet<>())) {
            ICitationConceptTag citTag = new CitationConceptTag();
            citTag.setConceptName(tag.getConceptName());
            citTag.setConceptUri(tag.getConceptUri());
            citTag.setLocalConceptId(tag.getLocalConceptId());
            citTag.setLocalConceptTypeId(tag.getLocalConceptTypeId());
            citTag.setTypeName(tag.getTypeName());
            citTag.setTypeUri(tag.getTypeUri());
            citation.getConceptTags().add(citTag);
        }
    }
}
