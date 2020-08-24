package edu.asu.diging.citesphere.core.factory.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Creator;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.Item;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.asu.diging.citesphere.core.factory.ICitationFactory;
import edu.asu.diging.citesphere.core.factory.ZoteroConstants;
import edu.asu.diging.citesphere.core.sync.ExtraData;
import edu.asu.diging.citesphere.core.util.IDateParser;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.IReference;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationConceptTag;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.model.bib.impl.Reference;

@Component
public class CitationFactory implements ICitationFactory {

    @Autowired
    private IDateParser dateParser;

    private List<BiConsumer<JsonObject, ICitation>> processFunctions;

    @PostConstruct
    public void init() {
        processFunctions = new ArrayList<BiConsumer<JsonObject, ICitation>>();
        processFunctions.add(this::processAuthors);
        processFunctions.add(this::processEditors);
        processFunctions.add(this::processConceptTags);
        processFunctions.add(this::processOtherCreators);
        processFunctions.add(this::processReferences);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.factory.impl.ICitationFactory#createCitation(
     * org.springframework.social.zotero.api.Item)
     */
    @Override
    public ICitation createCitation(Item item) {
        Data data = item.getData();
        ICitation citation = new Citation();
        citation.setIssue(data.getIssue());
        citation.setItemType(ItemType.getByZoteroKey(data.getItemType()));
        citation.setKey(item.getKey());
        citation.setPages(data.getPages());
        citation.setPublicationTitle(data.getPublicationTitle());
        citation.setSeries(data.getSeries());
        citation.setSeriesTitle(data.getSeriesTitle());
        citation.setTitle(data.getTitle());
        citation.setVolume(data.getVolume());
        citation.setVersion(data.getVersion());

        Set<IPerson> authors = new TreeSet<>();
        Set<IPerson> editors = new TreeSet<>();
        Set<ICreator> creators = new TreeSet<>();
        if (data.getCreators() != null) {
            int authorPos = 0;
            int editorPos = 0;
            int creatorPos = 0;
            for (Creator c : data.getCreators()) {
                if (c.getCreatorType().equals(ZoteroConstants.CREATOR_TYPE_AUTHOR)) {
                    authors.add(createPerson(c, authorPos));
                    authorPos++;
                } else if (c.getCreatorType().equals(ZoteroConstants.CREATOR_TYPE_EDITOR)) {
                    editors.add(createPerson(c, editorPos));
                    editorPos++;
                } else {
                    creators.add(createCreator(c, creatorPos));
                    creatorPos++;
                }
            }
        }
        citation.setAuthors(authors);
        citation.setEditors(editors);
        citation.setOtherCreators(creators);
        citation.setDateFreetext(item.getData().getDate());
        if (item.getData().getDate() != null) {
            citation.setDate(dateParser.parse(item.getData().getDate()).toString());
        }
        citation.setUrl(item.getData().getUrl());

        citation.setAbstractNote(item.getData().getAbstractNote());
        citation.setAccessDate(item.getData().getAccessDate());
        citation.setArchive(item.getData().getArchive());
        citation.setArchiveLocation(item.getData().getArchiveLocation());
        citation.setCallNumber(item.getData().getCallNumber());
        citation.setDoi(item.getData().getDoi());
        citation.setIssn(item.getData().getIssn());
        citation.setJournalAbbreviation(item.getData().getJournalAbbreviation());
        citation.setLanguage(item.getData().getLanguage());
        citation.setLibraryCatalog(item.getData().getLibraryCatalog());
        citation.setRights(item.getData().getRights());
        citation.setSeriesText(item.getData().getSeriesText());
        citation.setShortTitle(item.getData().getShortTitle());

        citation.setDateAdded(item.getData().getDateAdded());

        parseExtra(data, citation);
        return citation;
    }

    private IPerson createPerson(Creator creator, int index) {
        IPerson person = new Person();
        person.setFirstName(creator.getFirstName());
        person.setLastName(creator.getLastName());
        person.setName(String.join(" ", creator.getFirstName(), creator.getLastName()));
        person.setPositionInList(index);
        return person;
    }

    private ICreator createCreator(Creator zcreator, int index) {
        ICreator creator = new edu.asu.diging.citesphere.model.bib.impl.Creator();
        creator.setPerson(createPerson(zcreator, index));
        creator.setRole(zcreator.getCreatorType());
        return creator;
    }

    private void parseExtra(Data data, ICitation citation) {
        initializeExtraCollections(citation);
        if (data.getExtra() == null) {
            return;
        }

        String extra = data.getExtra();
        citation.setExtra(extra);
        String citespherePattern = ExtraData.CITESPHERE_PATTERN;
        Pattern pattern = Pattern.compile(citespherePattern);
        Matcher match = pattern.matcher(extra);
        if (match.find()) {
            String extraMatch = match.group(1);
            JsonParser parser = new JsonParser();
            JsonObject jObj = parser.parse(extraMatch).getAsJsonObject();

            for (BiConsumer<JsonObject, ICitation> processFunction : processFunctions) {
                processFunction.accept(jObj, citation);
            }
        }
    }
    
    private void initializeExtraCollections(ICitation citation) {
        citation.setReferences(new HashSet<>());
        citation.setConceptTags(new HashSet<>());
        
    }

    private void processAuthors(JsonObject jObj, ICitation citation) {
        if (jObj.has("authors") && !jObj.get("authors").isJsonNull()) {
            JsonArray authors = jObj.get("authors").getAsJsonArray();
            mapPersonFields(authors, citation.getAuthors());
        }
    }

    private void processEditors(JsonObject jObj, ICitation citation) {
        if (jObj.has("editors") && !jObj.get("editors").isJsonNull()) {
            JsonArray editors = jObj.get("editors").getAsJsonArray();
            mapPersonFields(editors, citation.getEditors());
        }
    }

    private void processOtherCreators(JsonObject jObj, ICitation citation) {
        if (jObj.has("otherCreators") && !jObj.get("otherCreators").isJsonNull()) {
            JsonArray creators = jObj.get("otherCreators").getAsJsonArray();
            mapCreatorFields(creators, citation.getOtherCreators());
        }
    }

    private void processConceptTags(JsonObject jObj, ICitation citation) {
        if (jObj.has("conceptTags") && !jObj.get("conceptTags").isJsonNull()) {
            JsonArray conceptTags = jObj.get("conceptTags").getAsJsonArray();
            mapConceptTags(conceptTags, citation.getConceptTags());
        }
    }

    private void processReferences(JsonObject jObj, ICitation citation) {
        if (jObj.has("references") && !jObj.get("references").isJsonNull()) {
            JsonArray references = jObj.get("references").getAsJsonArray();
            references.forEach(ref -> {
                IReference reference = new Reference();
                citation.getReferences().add(reference);
                reference.setAuthorString((ref.getAsJsonObject().get("authorString") != null
                        && !ref.getAsJsonObject().get("authorString").isJsonNull())
                                ? ref.getAsJsonObject().get("authorString").getAsString()
                                : "");
                if (ref.getAsJsonObject().get("contributors") != null && !ref.getAsJsonObject().get("contributors").isJsonNull() && ref.getAsJsonObject().get("contributors").isJsonArray()) {
                    JsonArray contributors = ref.getAsJsonObject().get("contributors").getAsJsonArray();
                    Set<ICreator> refCreators = new HashSet<>();
                    reference.setContributors(refCreators);
                    contributors.forEach(c -> mapCreatorFields(contributors, refCreators));
                }
                reference.setTitle((ref.getAsJsonObject().get("title") != null
                        && !ref.getAsJsonObject().get("title").isJsonNull())
                                ? ref.getAsJsonObject().get("title").getAsString()
                                : "");
                reference.setEndPage((ref.getAsJsonObject().get("endPage") != null
                        && !ref.getAsJsonObject().get("endPage").isJsonNull())
                                ? ref.getAsJsonObject().get("endPage").getAsString()
                                : "");
                reference.setFirstPage((ref.getAsJsonObject().get("firstPage") != null
                        && !ref.getAsJsonObject().get("firstPage").isJsonNull())
                                ? ref.getAsJsonObject().get("firstPage").getAsString()
                                : "");
                reference.setIdentifier((ref.getAsJsonObject().get("identifier") != null
                        && !ref.getAsJsonObject().get("identifier").isJsonNull())
                                ? ref.getAsJsonObject().get("identifier").getAsString()
                                : "");
                reference.setIdentifierType((ref.getAsJsonObject().get("identifierType") != null
                        && !ref.getAsJsonObject().get("identifierType").isJsonNull())
                                ? ref.getAsJsonObject().get("identifierType").getAsString()
                                : "");
                reference.setReferenceString((ref.getAsJsonObject().get("referenceString") != null
                        && !ref.getAsJsonObject().get("referenceString").isJsonNull())
                                ? ref.getAsJsonObject().get("referenceString").getAsString()
                                : "");
                reference.setReferenceStringRaw((ref.getAsJsonObject().get("referenceStringRaw") != null
                        && !ref.getAsJsonObject().get("referenceStringRaw").isJsonNull())
                                ? ref.getAsJsonObject().get("referenceStringRaw").getAsString()
                                : "");
                reference.setSource((ref.getAsJsonObject().get("source") != null
                        && !ref.getAsJsonObject().get("source").isJsonNull())
                                ? ref.getAsJsonObject().get("source").getAsString()
                                : "");
                reference.setVolume((ref.getAsJsonObject().get("volume") != null
                        && !ref.getAsJsonObject().get("volume").isJsonNull())
                                ? ref.getAsJsonObject().get("volume").getAsString()
                                : "");
                reference.setYear((ref.getAsJsonObject().get("year") != null
                        && !ref.getAsJsonObject().get("year").isJsonNull())
                                ? ref.getAsJsonObject().get("year").getAsString()
                                : "");
                reference.setPublicationType((ref.getAsJsonObject().get("publicationType") != null
                        && !ref.getAsJsonObject().get("publicationType").isJsonNull())
                                ? ref.getAsJsonObject().get("publicationType").getAsString()
                                : "");
                reference.setCitationId((ref.getAsJsonObject().get("citationId") != null
                        && !ref.getAsJsonObject().get("citationId").isJsonNull())
                                ? ref.getAsJsonObject().get("citationId").getAsString()
                                : "");
                reference.setReferenceId((ref.getAsJsonObject().get("referenceId") != null
                        && !ref.getAsJsonObject().get("referenceId").isJsonNull())
                                ? ref.getAsJsonObject().get("referenceId").getAsString()
                                : "");
                reference.setReferenceLabel((ref.getAsJsonObject().get("referenceLabel") != null
                        && !ref.getAsJsonObject().get("referenceLabel").isJsonNull())
                                ? ref.getAsJsonObject().get("referenceLabel").getAsString()
                                : "");
            });
        }
    }

    private void mapCreatorFields(JsonArray creatorList, Set<ICreator> citationCreatorList) {
        List<edu.asu.diging.citesphere.model.bib.impl.Creator> extraCreatorList = new ArrayList<>();
        List<String> personNames = new ArrayList<>();
        creatorList.forEach(a -> {
            ICreator creator = (ICreator) new edu.asu.diging.citesphere.model.bib.impl.Creator();
            creator.setRole((a.getAsJsonObject().get("role") != null && !a.getAsJsonObject().get("role").isJsonNull())
                    ? a.getAsJsonObject().get("role").getAsString()
                    : "");
            creator.setPerson(new Person());
            if (a.getAsJsonObject().get("person") != null && !a.getAsJsonObject().get("person").isJsonNull()) {
                mapPerson(a.getAsJsonObject().get("person"), creator.getPerson());
            }
            personNames.add(creator.getPerson().getFirstName() + creator.getPerson().getLastName());
            extraCreatorList.add((edu.asu.diging.citesphere.model.bib.impl.Creator) creator);
        });
        for (Iterator<ICreator> iterator = citationCreatorList.iterator(); iterator.hasNext();) {
            ICreator creator = iterator.next();
            if (personNames.contains(creator.getPerson().getFirstName() + creator.getPerson().getLastName())) {
                iterator.remove();
            }
        }
        extraCreatorList.forEach(a -> citationCreatorList.add(a));
    }

    private void mapPersonFields(JsonArray personList, Set<IPerson> citationPersonList) {
        List<Person> extraPersonList = new ArrayList<>();
        List<String> personNames = new ArrayList<>();
        personList.forEach(a -> {
            IPerson person = new Person();
            mapPerson(a, person);
            personNames.add(person.getFirstName() + person.getLastName());
            extraPersonList.add((Person) person);
        });

        for (Iterator<IPerson> iterator = citationPersonList.iterator(); iterator.hasNext();) {
            IPerson person = iterator.next();
            if (personNames.contains(person.getFirstName() + person.getLastName())) {
                iterator.remove();
            }
        }
        extraPersonList.forEach(a -> citationPersonList.add(a));
    }

    private void mapPerson(JsonElement a, IPerson person) {
        person.setName(a.getAsJsonObject().get("name") != null && !a.getAsJsonObject().get("name").isJsonNull()
                ? a.getAsJsonObject().get("name").getAsString()
                : "");
        person.setFirstName(
                a.getAsJsonObject().get("firstName") != null && !a.getAsJsonObject().get("firstName").isJsonNull()
                        ? a.getAsJsonObject().get("firstName").getAsString()
                        : "");
        person.setLastName(
                a.getAsJsonObject().get("lastName") != null && !a.getAsJsonObject().get("lastName").isJsonNull()
                        ? a.getAsJsonObject().get("lastName").getAsString()
                        : "");
        person.setUri(a.getAsJsonObject().get("uri") != null && !a.getAsJsonObject().get("uri").isJsonNull()
                ? a.getAsJsonObject().get("uri").getAsString()
                : "");
        person.setLocalAuthorityId(a.getAsJsonObject().get("localAuthorityId") != null
                && !a.getAsJsonObject().get("localAuthorityId").isJsonNull()
                        ? a.getAsJsonObject().get("localAuthorityId").getAsString()
                        : "");
        person.setAffiliations(new HashSet<>());
        JsonElement affiliations = a.getAsJsonObject().get("affiliations");
        if (affiliations instanceof JsonArray) {
            affiliations.getAsJsonArray().forEach(af -> {
                Affiliation affiliation = new Affiliation();
                affiliation.setName(
                        af.getAsJsonObject().get("name") != null && !af.getAsJsonObject().get("name").isJsonNull()
                                ? af.getAsJsonObject().get("name").getAsString()
                                : "");
                affiliation
                        .setUri(af.getAsJsonObject().get("uri") != null && !af.getAsJsonObject().get("uri").isJsonNull()
                                ? af.getAsJsonObject().get("uri").getAsString()
                                : "");
                person.getAffiliations().add(affiliation);
            });
        }
    }

    private void mapConceptTags(JsonArray conceptArray, Set<ICitationConceptTag> conceptTags) {
        conceptArray.forEach(c -> {
            ICitationConceptTag tag = new CitationConceptTag();
            JsonObject conceptJsonObject = c.getAsJsonObject();
            tag.setConceptName(
                    conceptJsonObject.get("conceptName") != null && !conceptJsonObject.get("conceptName").isJsonNull()
                            ? conceptJsonObject.get("conceptName").getAsString()
                            : "");
            tag.setTypeName(conceptJsonObject.get("typeName") != null && !conceptJsonObject.get("typeName").isJsonNull()
                    ? conceptJsonObject.get("typeName").getAsString()
                    : "");

            tag.setConceptUri(
                    conceptJsonObject.get("conceptUri") != null && !conceptJsonObject.get("conceptUri").isJsonNull()
                            ? conceptJsonObject.get("conceptUri").getAsString()
                            : null);
            tag.setLocalConceptId(conceptJsonObject.get("localConceptId") != null
                    && !conceptJsonObject.get("localConceptId").isJsonNull()
                            ? conceptJsonObject.get("localConceptId").getAsString()
                            : null);

            tag.setTypeUri(conceptJsonObject.get("typeUri") != null && !conceptJsonObject.get("typeUri").isJsonNull()
                    ? conceptJsonObject.get("typeUri").getAsString()
                    : null);
            tag.setLocalConceptTypeId(conceptJsonObject.get("localConceptTypeId") != null
                    && !conceptJsonObject.get("localConceptTypeId").isJsonNull()
                            ? conceptJsonObject.get("localConceptTypeId").getAsString()
                            : null);

            conceptTags.add(tag);
        });

    }
}
