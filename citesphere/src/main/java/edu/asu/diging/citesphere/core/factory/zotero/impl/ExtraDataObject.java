package edu.asu.diging.citesphere.core.factory.zotero.impl;

import java.util.Set;
import edu.asu.diging.citesphere.core.model.bib.IPerson;

public class ExtraDataObject {

    private Set<IPerson> authors;

    private Set<IPerson> editors;

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

}
