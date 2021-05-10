package edu.asu.diging.citesphere.api.v1.apps;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class ApplicationsInfoController extends V1Controller {

    @Autowired
    private IOAuthClientManager clientManager;
    
    @GetMapping("/admin/apps")
    public ResponseEntity<List<AppInfo>> getAllApps(Principal principal) {

        //Checks if the user is of role trusted client
        boolean flag = false;
        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication userDetails = (OAuth2Authentication) principal;
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                if (authority.getAuthority().equals(Role.TRUSTED_CLIENT)) {
                    flag = true;
                    break;
                }
            }
        }

        //If the user is not of expected role, respond as unauthorized access 
        if (!flag)
            return new ResponseEntity<List<AppInfo>>(HttpStatus.UNAUTHORIZED);

        //Fetch all the applications
        List<OAuthClient> apps = clientManager.getAllApps();
        List<AppInfo> appsInfo = new ArrayList<>();

        //Transform the applications to data transfer objects
        for (OAuthClient authClient : apps) {
            AppInfo appInfo = new AppInfo();
            appInfo.setClientId(authClient.getClientId());
            appInfo.setName(authClient.getName());
            appInfo.setDescription(authClient.getDescription());
            String applicationType = authClient.getAuthorizedGrantTypes().contains("authorization_code")?"authorization_code":"client_credentials";
            appInfo.setApplicationType(applicationType);
            appsInfo.add(appInfo);
        }

        if (appsInfo.size() == 0)
            return new ResponseEntity<List<AppInfo>>(HttpStatus.NOT_FOUND);

        //Return the application details
        return new ResponseEntity<List<AppInfo>>(appsInfo, HttpStatus.OK);
    }

    //Data transfer object for application details
    class AppInfo {
        private String clientId;
        private String name;
        private String description;
        private String applicationType;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

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

        public String getApplicationType() {
            return applicationType;
        }

        public void setApplicationType(String applicationType) {
            this.applicationType = applicationType;
        }
    }

}
