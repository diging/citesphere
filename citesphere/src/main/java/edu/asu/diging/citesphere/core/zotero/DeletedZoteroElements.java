package edu.asu.diging.citesphere.core.zotero;

import java.util.List;

public class DeletedZoteroElements {
    
    private List<String> collections;
    private List<String> items;
    private List<String> tags;
    
    public List<String> getCollections() {
        return collections;
    }
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
    public List<String> getItems() {
        return items;
    }
    public void setItems(List<String> items) {
        this.items = items;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
