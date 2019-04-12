package edu.asu.diging.citesphere.core.factory.zotero;

import org.springframework.social.zotero.api.Item;
import edu.asu.diging.citesphere.core.model.bib.ICitation;

public interface IItemFactory {

    Item createItem(ICitation citation);

}