package edu.asu.diging.citesphere.web.admin.forms;

public class AppForm {

    private String name;
    private String description;
    private String grantType;
    private String redirectUrl;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getGrantType() {
        return grantType;
    }
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
    public String getRedirectUrl() {
        return redirectUrl;
    }
    public void setRedirectUrl(String callbackUrl) {
        this.redirectUrl = callbackUrl;
    }  
    
}
