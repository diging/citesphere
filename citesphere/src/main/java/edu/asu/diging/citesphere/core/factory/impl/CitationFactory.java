package edu.asu.diging.citesphere.core.factory.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Creator;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.Item;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.factory.ICitationFactory;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;
import edu.asu.diging.citesphere.core.model.bib.impl.Person;
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
        
        List<IPerson> authors = new ArrayList<>();
        List<IPerson> editors = new ArrayList<>();
        if (data.getCreators() != null) {
            data.getCreators().forEach(c -> {
                if (c.getCreatorType().equals(AUTHOR)) {
                    authors.add(createPerson(c));
                } else if (c.getCreatorType().equals(EDITOR)) {
                    editors.add(createPerson(c));
                }
            });
        }
        citation.setAuthors(authors);
        citation.setEditors(editors);
        citation.setDateFreetext(item.getData().getDate());
        if (item.getData().getDate() != null) {
            citation.setDate(dateParser.parse(item.getData().getDate()));
        }
        citation.setUrl(item.getData().getUrl());
        return citation;
    }
    
    private IPerson createPerson(Creator creator) {
        IPerson person = new Person();
        person.setFirstName(creator.getFirstName());
        person.setLastName(creator.getLastName());
        person.setName(String.join(" ", creator.getFirstName(), creator.getLastName()));
        return person;
    }
    
}
