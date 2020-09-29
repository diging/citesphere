package edu.asu.diging.citesphere.core.zotero;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;

public class ZoteroGroupItemsResponse {

    private List<ICitation> citations;
    private long contentVersion;
    
    public List<ICitation> getCitations() {
        return citations;
    }
    public void setCitations(List<ICitation> citations) {
        this.citations = citations;
    }
    public long getContentVersion() {
        return contentVersion;
    }
    public void setContentVersion(long contentVersion) {
        this.contentVersion = contentVersion;
    }
    
}
