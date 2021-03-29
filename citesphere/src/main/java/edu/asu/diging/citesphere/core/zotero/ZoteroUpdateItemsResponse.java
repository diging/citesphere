package edu.asu.diging.citesphere.core.zotero;

import java.util.List;

public class ZoteroUpdateItemsResponse{
    private List<String> successItems;
    private List<String> failedItems;
    private List<String> unchagedItems;
    
    public void setSuccessItems(List<String> successItems) {
        this.successItems = successItems;
    }
    
    public List<String> getSuccessItems() {
        return successItems;
    }
    
    public void setFailedItems(List<String> failedItems) {
        this.failedItems = failedItems;
    }
    
    public List<String> getFailedItems() {
        return failedItems;
    }
    
    public void setUnchangedItems(List<String> unchagedItems) {
        this.unchagedItems = unchagedItems;
    }
    
    public List<String> getUnchangedItems() {
        return unchagedItems;
    }
    
}