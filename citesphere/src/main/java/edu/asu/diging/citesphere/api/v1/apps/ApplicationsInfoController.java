package edu.asu.diging.citesphere.api.v1.apps;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        // Checks if the user is of role trusted client
        boolean isAuthorized = false;
        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication userDetails = (OAuth2Authentication) principal;
            isAuthorized = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(Role.TRUSTED_CLIENT));
        }

        // If the user is not of expected role, respond as forbidden access
        if (!isAuthorized) {
            return new ResponseEntity<List<AppInfo>>(HttpStatus.FORBIDDEN);
        }

        List<OAuthClient> apps = clientManager.getAllApps();
        List<AppInfo> appsInfo = new ArrayList<>();

        apps.forEach(authClient -> {
            AppInfo appInfo = new AppInfo();
            appInfo.setClientId(authClient.getClientId());
            appInfo.setName(authClient.getName());
            appInfo.setDescription(authClient.getDescription());
            appInfo.setApplicationTypes(authClient.getAuthorizedGrantTypes());
            appsInfo.add(appInfo);
        });

        return new ResponseEntity<List<AppInfo>>(appsInfo, HttpStatus.OK);
    }

    /**
     * Data Transfer Object used to retrieve all registered Applications.
     * 
     * @author mlimbadi
     *
     */
    class AppInfo {
        private String clientId;
        private String name;
        private String description;
        private Set<String> applicationTypes;

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

        public Set<String> getApplicationTypes() {
            return applicationTypes;
        }

        public void setApplicationTypes(Set<String> applicationTypes) {
            this.applicationTypes = applicationTypes;
        }
    }

}
