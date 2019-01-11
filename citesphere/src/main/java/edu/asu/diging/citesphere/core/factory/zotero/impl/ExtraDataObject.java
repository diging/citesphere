package edu.asu.diging.citesphere.core.factory.zotero.impl;

import java.util.Set;

import edu.asu.diging.citesphere.core.model.bib.IPerson;

public class ExtraDataObject {

    private Set<IPerson> authors;

    public Set<IPerson> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<IPerson> authors) {
        this.authors = authors;
    }
}
