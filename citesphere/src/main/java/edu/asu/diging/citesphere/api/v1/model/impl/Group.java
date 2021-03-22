package edu.asu.diging.citesphere.api.v1.model.impl;

public class Group {
    private long id;
    private String name;
    private long version;
    private String created;
    private String lastModified;
    private long numItems;
    private long owner;
    private String type;
    private String description;
    private String url;
    private SyncInfo syncInfo;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public String getLastModified() {
        return lastModified;
    }
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    public long getNumItems() {
        return numItems;
    }
    public void setNumItems(long numItems) {
        this.numItems = numItems;
    }
    public long getOwner() {
        return owner;
    }
    public void setOwner(long owner) {
        this.owner = owner;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public SyncInfo getSyncInfo() {
        return syncInfo;
    }
    public void setSyncInfo(SyncInfo syncInfo) {
        this.syncInfo = syncInfo;
    }
}