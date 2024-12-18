package edu.asu.diging.citesphere.core.factory.zotero;

import java.util.List;

import org.springframework.social.zotero.api.Item;

import edu.asu.diging.citesphere.model.bib.ICitation;

public interface IItemFactory {

    Item createItem(ICitation citation, List<String> collectionIds);

    Item createMetaDataItem(ICitation citation);
    
}