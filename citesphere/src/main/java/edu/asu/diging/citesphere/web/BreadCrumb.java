package edu.asu.diging.citesphere.web;

public class BreadCrumb {

    private String label;
    private BreadCrumbType type;
    private String objectId;
    private Object object;
    
    public BreadCrumb(String label, BreadCrumbType type, String objectId, Object object) {
        super();
        this.label = label;
        this.type = type;
        this.objectId = objectId;
        this.object = object;
    }
    
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public BreadCrumbType getType() {
        return type;
    }
    public void setType(BreadCrumbType type) {
        this.type = type;
    }
    public String getObjectId() {
        return objectId;
    }
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
