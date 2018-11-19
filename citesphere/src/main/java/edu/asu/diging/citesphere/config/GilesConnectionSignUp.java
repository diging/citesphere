package edu.asu.diging.citesphere.config;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import edu.asu.diging.citesphere.core.user.IUserHelper;

public class GilesConnectionSignUp implements ConnectionSignUp {
    
     private IUserHelper userHelper;

    public GilesConnectionSignUp(IUserHelper userHelper) {
        this.userHelper = userHelper;
    }
 
    public String execute(Connection<?> connection) {
        
        return userHelper.createUser(connection);
    }

}