package edu.asu.diging.citesphere.core.factory.zotero.impl;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.zotero.api.Creator;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.Item;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.asu.diging.citesphere.core.factory.ZoteroConstants;
import edu.asu.diging.citesphere.core.factory.zotero.IItemFactory;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.sync.ExtraData;

@Component
public class ItemFactory implements IItemFactory {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.factory.zotero.impl.IItemFactory#createItem(edu.asu.diging.citesphere.core.model.bib.ICitation)
     */
    @Override
    public Item createItem(ICitation citation) {
        Item item = new Item();
        item.setKey(citation.getKey());
        
        Data data = new Data();
        item.setData(data);
        
        data.setKey(citation.getKey());
        data.setAbstractNote(citation.getAbstractNote());
        data.setAccessDate(citation.getAccessDate());
        data.setArchive(citation.getArchive());
        data.setArchiveLocation(citation.getArchiveLocation());
        data.setCallNumber(citation.getCallNumber());
        data.setDate(citation.getDateFreetext());
        data.setDateAdded(citation.getDateAdded());
        data.setDateModified(citation.getDateModified());
        data.setDoi(citation.getDoi());
        data.setIssn(citation.getIssn());
        data.setIssue(citation.getIssue());
        data.setItemType(citation.getItemType().getZoteroKey());
        data.setJournalAbbreviation(citation.getJournalAbbreviation());
        data.setLanguage(citation.getLanguage());
        data.setLibraryCatalog(citation.getLibraryCatalog());
        data.setPages(citation.getPages());
        data.setPublicationTitle(citation.getPublicationTitle());
        data.setRights(citation.getRights());
        data.setSeries(citation.getSeries());
        data.setSeriesTitle(citation.getSeriesTitle());
        data.setSeriesText(citation.getSeriesText());
        data.setShortTitle(citation.getShortTitle());
        data.setTitle(citation.getTitle());
        data.setUrl(citation.getUrl());
        data.setVolume(citation.getVolume());
        data.setVersion(citation.getVersion());
        
        data.setCreators(new ArrayList<>());
        citation.getAuthors().forEach(a -> {
            Creator creator = new Creator();
            creator.setFirstName(a.getFirstName());
            creator.setLastName(a.getLastName());
            creator.setCreatorType(ZoteroConstants.CREATOR_TYPE_AUTHOR);
            data.getCreators().add(creator);
        });
        if (citation.getEditors() != null) {
            citation.getEditors().forEach(e -> {
                Creator creator = new Creator();
                creator.setFirstName(e.getFirstName());
                creator.setLastName(e.getLastName());
                creator.setCreatorType(ZoteroConstants.CREATOR_TYPE_EDITOR);
                data.getCreators().add(creator);
            });
        }
        
        try {
            writeExtraData(citation, data);
        } catch (JsonProcessingException e1) {
            // FIXME: hadnle this
            logger.error("Could not serialize extra data.", e1);
        }
        
        return item;
    }
    
    private void writeExtraData(ICitation citation, Data data) throws JsonProcessingException {
        
        ExtraDataObject extraDataObject = new ExtraDataObject();
        extraDataObject.setAuthors(citation.getAuthors());
        extraDataObject.setEditors(citation.getEditors());
        ObjectMapper mapper = new ObjectMapper();
        String extraDataAsJson = mapper.writer().writeValueAsString(extraDataObject);
        
        String extraData = "";
        String citesphereData = ExtraData.CITESPHERE_PREFIX + " " + extraDataAsJson + "\n";
        if (citation.getExtra() != null && !citation.getExtra().trim().isEmpty()) {
            // if extra field is already filled
            Pattern pattern = Pattern.compile(ExtraData.CITESPHERE_PATTERN);
            Matcher matcher = pattern.matcher(citation.getExtra());
            if (!matcher.find()) {
                // if there is no citesphere data yet, append citesphere data
                extraData = extraData + citesphereData;
            } else {
                // else replace citesphere data
                extraData = citation.getExtra().replaceAll(ExtraData.CITESPHERE_PATTERN, citesphereData);
            }
        } else {
            // if there is no extra data
            extraData = citesphereData;
        }
        
        data.setExtra(extraData);
    }
}