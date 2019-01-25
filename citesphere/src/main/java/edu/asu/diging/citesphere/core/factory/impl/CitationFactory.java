package edu.asu.diging.citesphere.core.factory.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;
import edu.asu.diging.citesphere.core.model.bib.impl.Person;
import edu.asu.diging.citesphere.core.sync.ExtraData;
import edu.asu.diging.citesphere.core.util.IDateParser;

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
        if (data.getCreators() != null) {
            int authorPos = 0;
            int editorPos = 0;
            for (Creator c : data.getCreators()) {
                if (c.getCreatorType().equals(ZoteroConstants.CREATOR_TYPE_AUTHOR)) {
                    authors.add(createPerson(c, authorPos));
                    authorPos++;
                } else if (c.getCreatorType().equals(ZoteroConstants.CREATOR_TYPE_EDITOR)) {
                    editors.add(createPerson(c, editorPos));
                    editorPos++;
                }
            }
        }
        citation.setAuthors(authors);
        citation.setEditors(editors);
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

            if(jObj.has("authors") && !jObj.get("authors").isJsonNull()) {
                
                List<Person> extraAuthors = new ArrayList<>();
                List<String> authorNames = new ArrayList<>();
                JsonArray authors = jObj.get("authors").getAsJsonArray();
                
                authors.forEach(a -> {
                    Person person = new Person();
                    person.setName(a.getAsJsonObject().get("name") != null && !a.getAsJsonObject().get("name").isJsonNull() ? a.getAsJsonObject().get("name").getAsString() : "");
                    person.setFirstName(a.getAsJsonObject().get("firstName") != null && !a.getAsJsonObject().get("firstName").isJsonNull() ? a.getAsJsonObject().get("firstName").getAsString() : "");
                    person.setLastName(a.getAsJsonObject().get("lastName") != null && !a.getAsJsonObject().get("lastName").isJsonNull() ? a.getAsJsonObject().get("lastName").getAsString() : "");
                    authorNames.add(person.getFirstName() + person.getLastName());
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
                    extraAuthors.add(person);
                });
                for (Iterator<IPerson> iterator = citation.getAuthors().iterator(); iterator.hasNext();) {
                    IPerson author = iterator.next();
                    if (authorNames.contains(author.getFirstName() + author.getLastName())) {
                        iterator.remove();
                    }
                }
                extraAuthors.forEach(a -> citation.getAuthors().add(a));
            }
            
            if(jObj.has("editors") && !jObj.get("editors").isJsonNull()) {
                List<Person> extraEditors = new ArrayList<>();
                List<String> editorNames = new ArrayList<>();
                JsonArray editors = jObj.get("editors").getAsJsonArray();
                editors.forEach(a -> {
                    Person person = new Person();
                    person.setName(a.getAsJsonObject().get("name") != null && !a.getAsJsonObject().get("name").isJsonNull() ? a.getAsJsonObject().get("name").getAsString() : "");
                    person.setFirstName(a.getAsJsonObject().get("firstName") != null && !a.getAsJsonObject().get("firstName").isJsonNull() ? a.getAsJsonObject().get("firstName").getAsString() : "");
                    person.setLastName(a.getAsJsonObject().get("lastName") != null && !a.getAsJsonObject().get("lastName").isJsonNull() ? a.getAsJsonObject().get("lastName").getAsString() : "");
                    editorNames.add(person.getFirstName() + person.getLastName());
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
                    person.getAffiliations().forEach(aff -> {System.out.println("person "+aff.getName());});
                    extraEditors.add(person);
                });

                for (Iterator<IPerson> iterator = citation.getEditors().iterator(); iterator.hasNext();) {
                    IPerson editor = iterator.next();
                    if (editorNames.contains(editor.getFirstName() + editor.getLastName())) {
                        iterator.remove();
                    }
                }
                extraEditors.forEach(a -> citation.getEditors().add(a));
            }
        }
    }
}
