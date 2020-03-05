package edu.asu.diging.citesphere.core.factory.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.factory.IUserFactory;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;
import edu.asu.diging.citesphere.web.forms.UserForm;

@Component
public class UserFactory implements IUserFactory {

    /* (non-Javadoc)
     * @see edu.asu.diging.sustainability.core.factory.impl.IUserFactory#createUser(edu.asu.diging.sustainability.web.pages.UserForm)
     */
    @Override
    public IUser createUser(UserForm userForm) {
        IUser user = new User();
        user.setEmail(userForm.getEmail());
        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setPassword(userForm.getPassword());
        user.setUsername(userForm.getUsername());
        
        return user;
    }
    
    @Override
    public IUser createUser(String username, String password, String role, boolean enabled) {
        IUser user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        Set<SimpleGrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(role));
        user.setRoles(roles);
        return user;
    }
}
