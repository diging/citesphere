package edu.asu.diging.citesphere.web.admin.forms;

public class UserAccessTokenForm {
    private String name;
    private String redirectUrl;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    public void setRedirectUrl(String callbackUrl) {
        this.redirectUrl = callbackUrl;
    }
}
