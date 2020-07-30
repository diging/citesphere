package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.JsonObject;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class UserInfoController extends V1Controller {
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping("/user")
    public ResponseEntity<UserInfo> getUserInfo(Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        if (user == null) {
            return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
        }
        
        UserInfo info = new UserInfo();
        info.setEmail(user.getEmail());
        info.setFirstName(user.getFirstName());
        info.setLastName(user.getLastName());
        info.setUsername(user.getUsername());
        
        return new ResponseEntity<UserInfo>(info, HttpStatus.OK);
    }
    
    class UserInfo {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
