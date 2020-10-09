package edu.asu.diging.citesphere.api.v1.model.impl;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;

public class Items {

    private Group group;
    private List<ICitation> items;
    
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
    public List<ICitation> getItems() {
        return items;
    }
    
    public void setItems(List<ICitation> items) {
        this.items = items;
    }

}
