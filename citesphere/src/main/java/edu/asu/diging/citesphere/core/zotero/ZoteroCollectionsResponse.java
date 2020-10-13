package edu.asu.diging.citesphere.core.zotero;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitationCollection;

public class ZoteroCollectionsResponse {

    private List<ICitationCollection> collections;
    private long contentVersion;
    
    public List<ICitationCollection> getCollections() {
        return collections;
    }
    public void setCollections(List<ICitationCollection> citations) {
        this.collections = citations;
    }
    public long getContentVersion() {
        return contentVersion;
    }
    public void setContentVersion(long contentVersion) {
        this.contentVersion = contentVersion;
    }
    
}
