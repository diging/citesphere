package edu.asu.diging.citesphere.core.factory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;
import edu.asu.diging.citesphere.core.model.bib.impl.Person;
import edu.asu.diging.citesphere.core.sync.ExtraData;
import edu.asu.diging.citesphere.core.util.IDateParser;
import edu.asu.diging.citesphere.web.forms.PersonForm;

@Component
public class CitationFactory implements ICitationFactory {
    
    @Autowired
    private IDateParser dateParser;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.factory.impl.ICitationFactory#createCitation(org.springframework.social.zotero.api.Item)
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
            citation.setDate(dateParser.parse(item.getData().getDate()));
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
        ICreator creator = new edu.asu.diging.citesphere.core.model.bib.impl.Creator();
        creator.setPerson(createPerson(zcreator, index));
        creator.setRole(zcreator.getCreatorType());
        return creator;
    }
    
    private void parseExtra(Data data, ICitation citation) {
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
            if (jObj.has("authors") && !jObj.get("authors").isJsonNull()) {
                JsonArray authors = jObj.get("authors").getAsJsonArray();
                mapPersonFields(authors, citation.getAuthors());
            }
            if (jObj.has("editors") && !jObj.get("editors").isJsonNull()) {
                JsonArray editors = jObj.get("editors").getAsJsonArray();
                mapPersonFields(editors, citation.getEditors());
            }
            if (jObj.has("otherCreators") && !jObj.get("otherCreators").isJsonNull()) {
                JsonArray creators = jObj.get("otherCreators").getAsJsonArray();
                mapCreatorFields(creators, citation.getOtherCreators());
            }
        }
    }

    void mapCreatorFields(JsonArray creatorList, Set<ICreator> citationCreatorList) {
        List<Creator> extraCreatorList = new ArrayList<>();
        List<String> personNames = new ArrayList<>();
        creatorList.forEach(a -> {
            ICreator creator = (ICreator) new edu.asu.diging.citesphere.core.model.bib.impl.Creator();
            creator.setRole((a.getAsJsonObject().get("role") != null && !a.getAsJsonObject().get("role").isJsonNull()) ? a.getAsJsonObject().get("role").getAsString() : "");
            creator.setPerson(new Person());
            mapPerson(a, creator.getPerson());
            personNames.add(creator.getPerson().getFirstName() + creator.getPerson().getLastName());
        });
        for (Iterator<ICreator> iterator = citationCreatorList.iterator(); iterator.hasNext();) {
            ICreator creator = iterator.next();
            if (personNames.contains(creator.getPerson().getFirstName() + creator.getPerson().getLastName())) {
                iterator.remove();
            }
        }
        extraCreatorList.forEach(a -> citationCreatorList.add((ICreator) a));
    }
    
    private void mapPersonFields(JsonArray personList, Set<IPerson> citationPersonList) {
        List<Person> extraPersonList = new ArrayList<>();
        List<String> personNames = new ArrayList<>();
        personList.forEach(a -> {
            IPerson person = new Person();
            mapPerson(a, person);
            personNames.add(person.getFirstName() + person.getLastName());
            extraPersonList.add((Person)person);
        });

        for (Iterator<IPerson> iterator = citationPersonList.iterator(); iterator.hasNext();) {
            IPerson person = iterator.next();
            if (personNames.contains(person.getFirstName() + person.getLastName())) {
                iterator.remove();
            }
        }
        extraPersonList.forEach(a -> citationPersonList.add(a));
    }
    
    void mapPerson(JsonElement a, IPerson person) {
        person.setName(a.getAsJsonObject().get("name") != null && !a.getAsJsonObject().get("name").isJsonNull() ? a.getAsJsonObject().get("name").getAsString() : "");
        person.setFirstName(a.getAsJsonObject().get("firstName") != null && !a.getAsJsonObject().get("firstName").isJsonNull() ? a.getAsJsonObject().get("firstName").getAsString() : "");
        person.setLastName(a.getAsJsonObject().get("lastName") != null && !a.getAsJsonObject().get("lastName").isJsonNull() ? a.getAsJsonObject().get("lastName").getAsString() : "");
        person.setUri(a.getAsJsonObject().get("uri")!=null && !a.getAsJsonObject().get("uri").isJsonNull() ? a.getAsJsonObject().get("uri").getAsString():"");
        person.setLocalAuthorityId(a.getAsJsonObject().get("localAuthorityId")!=null && !a.getAsJsonObject().get("localAuthorityId").isJsonNull() ? a.getAsJsonObject().get("localAuthorityId").getAsString() : "");
        person.setAffiliations(new HashSet<>());
        JsonElement affiliations = a.getAsJsonObject().get("affiliations");
        if (affiliations instanceof JsonArray) {
            affiliations.getAsJsonArray().forEach(af -> {
                Affiliation affiliation = new Affiliation();
                affiliation.setName(af.getAsJsonObject().get("name") != null && !af.getAsJsonObject().get("name").isJsonNull() ? af.getAsJsonObject().get("name").getAsString() : "");
                affiliation.setUri(af.getAsJsonObject().get("uri") != null && !af.getAsJsonObject().get("uri").isJsonNull() ? af.getAsJsonObject().get("uri").getAsString() : "");
                person.getAffiliations().add(affiliation);
            });
        }
    }
}
