package edu.asu.diging.citesphere.core.factory;

import org.springframework.social.zotero.api.Item;

import edu.asu.diging.citesphere.core.model.bib.ICitation;

public interface ICitationFactory {

    String AUTHOR = "author";
    String EDITOR = "editor";

    ICitation createCitation(Item item);

}