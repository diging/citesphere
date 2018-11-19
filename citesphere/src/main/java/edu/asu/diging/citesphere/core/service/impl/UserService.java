package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

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
import edu.asu.diging.citesphere.core.factory.IUserFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.impl.User;
import edu.asu.diging.citesphere.core.repository.UserRepository;
import edu.asu.diging.citesphere.core.service.IUserManager;


@Transactional
@Service("citesphereUserDetailsService")
@PropertySource("classpath:/user.properties")
public class UserService implements UserDetailsService, IUserManager {
    
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
        user.setNotes(user.getNotes() + String.format("Approved by %s.", approver));
        if (((IUser)user).getRoles() == null) {
            ((IUser)user).setRoles(new HashSet<>());
        }
        ((IUser)user).getRoles().add(new SimpleGrantedAuthority(Role.USER));
        userRepository.save((User)user);
    }
}
