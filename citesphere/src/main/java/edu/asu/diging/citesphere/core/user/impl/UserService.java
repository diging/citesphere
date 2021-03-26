package edu.asu.diging.citesphere.core.user.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.citesphere.core.exceptions.UserAlreadyExistsException;
import edu.asu.diging.citesphere.core.exceptions.UserDoesNotExistException;
import edu.asu.diging.citesphere.core.factory.IUserFactory;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.repository.UserRepository;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;


@Transactional
@Service("citesphereUserDetailsService")
@PropertySource("classpath:/user.properties")
public class UserService implements UserDetailsService, IUserManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("usersFile")
    private Properties properties;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private IUserFactory userFactory;

    @Override
    public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
        logger.debug("Loading user: " + arg0);
        Optional<User> foundUser = userRepository.findById(arg0);
        if (foundUser.isPresent()) {
            return foundUser.get();
        }
        
        String userDetails = properties.getProperty(arg0);
        if (userDetails != null) {
            String[] details = userDetails.split(",");
            return (UserDetails) userFactory.createUser(arg0, details[0], details[1], (details[2].equals("enabled")));
        }
        
        throw new UsernameNotFoundException(String.format("No user with username %s found.", arg0));
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.sustainability.core.service.impl.IUserManager#save(edu.asu.diging.sustainability.core.model.impl.User)
     */
    @Override
    public void create(IUser user) throws UserAlreadyExistsException {
        Optional<User> existingUser = userRepository.findById(user.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("The user already exists.");
        }
        user.setEnabled(false);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save((User)user);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.sustainability.core.service.impl.IUserManager#findByUsername(java.lang.String)
     */
    @Override
    public IUser findByUsername(String username) {
        Optional<User> foundUser = userRepository.findById(username);
        if (foundUser.isPresent()) {
            return foundUser.get();
        }
        return null;
    }
    
    @Override
    public List<IUser> findAll() {
        Iterable<User> users = userRepository.findAll();
        List<IUser> results = new ArrayList<>();
        users.iterator().forEachRemaining(u -> results.add(u));
        return results;
    }
    
    @Override
    public void approveAccount(String username, String approver) {
        IUser user = findByUsername(username);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        if (user.getNotes() == null) {
            user.setNotes("");
        }
        user.setNotes(user.getNotes() + String.format("Approved by %s. ", approver));
        if (((IUser)user).getRoles() == null) {
            ((IUser)user).setRoles(new HashSet<>());
        }
        ((IUser)user).getRoles().add(new SimpleGrantedAuthority(Role.USER));
        userRepository.save((User)user);
    }
    
    @Override
    public void disableUser(String username, String initiator) {
        IUser user = findByUsername(username);
        user.setEnabled(false);
        user.setNotes(user.getNotes() + String.format("Disabled by %s. ", initiator));
        userRepository.save((User)user);
    }
    
    @Override
    public void addRole(String username, String initiator, String role) {
        IUser user = findByUsername(username);
        user.setNotes(user.getNotes() + String.format("User %s added role %s. ", initiator, role));
        if (((IUser)user).getRoles() == null) {
            ((IUser)user).setRoles(new HashSet<>());
        }
        ((IUser)user).getRoles().add(new SimpleGrantedAuthority(role));
        userRepository.save((User)user);
    }
    
    @Override
    public void removeRole(String username, String initiator, String role) {
        IUser user = findByUsername(username);
        user.setNotes(user.getNotes() + String.format("User %s removed role %s. ", initiator, role));
        if (user.getRoles() == null) {
            return;
        }
        SimpleGrantedAuthority roleToBeRemoved = null;
        for (SimpleGrantedAuthority authority : user.getRoles()) {
            if (authority.getAuthority().equals(role)) {
                roleToBeRemoved = authority;
                break;
            }
        }
        
        if (roleToBeRemoved != null) {
            user.getRoles().remove(roleToBeRemoved);
            userRepository.save((User)user);
        }
    }
    
    @Override
    public void changePassword(IUser user, String password) throws UserDoesNotExistException {
        Optional<User> existingUser = userRepository.findById(user.getUsername());
        if (!existingUser.isPresent()) {
            throw new UserDoesNotExistException("User " + user.getUsername() + " does not exist.");
        }
        user = existingUser.get();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save((User)user);
    }
    
    @Override
    public IUser findByEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }
}
