package edu.asu.diging.citesphere.api.v1.model.impl;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitationCollection;

public class Collections {

    private Group group;
    private List<ICitationCollection> collections;
    
    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public List<ICitationCollection> getCollections() {
        return collections;
    }
    public void setCollections(List<ICitationCollection> collections) {
        this.collections = collections;
    }
    
    
}
