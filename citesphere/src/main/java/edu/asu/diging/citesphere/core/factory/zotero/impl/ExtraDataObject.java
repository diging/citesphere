package edu.asu.diging.citesphere.core.factory.zotero.impl;

import java.util.Set;

import edu.asu.diging.citesphere.core.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IPerson;

public class ExtraDataObject {

    private Set<IPerson> authors;

    private Set<IPerson> editors;
    
    private Set<ICreator> otherCreators;
    
    private Set<ICitationConceptTag> conceptTags;

    public Set<ICreator> getOtherCreators() {
        return otherCreators;
    }

    public void setOtherCreators(Set<ICreator> otherCreators) {
        this.otherCreators = otherCreators;
    }

    public Set<IPerson> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<IPerson> authors) {
        this.authors = authors;
    }

    public Set<IPerson> getEditors() {
        return editors;
    }

    public void setEditors(Set<IPerson> editors) {
        this.editors = editors;
    }

    public Set<ICitationConceptTag> getConceptTags() {
        return conceptTags;
    }

    public void setConceptTags(Set<ICitationConceptTag> conceptTags) {
        this.conceptTags = conceptTags;
    }

}
