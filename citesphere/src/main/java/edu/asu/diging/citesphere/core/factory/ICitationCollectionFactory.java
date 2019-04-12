package edu.asu.diging.citesphere.core.factory;

import org.springframework.social.zotero.api.Collection;
import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;

public interface ICitationCollectionFactory {

    ICitationCollection createCitationCollection(Collection collection);

}