package edu.asu.diging.citesphere.core.model.impl;

import java.util.Collection;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.Role;

@Entity
public class User implements UserDetails, IUser {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String zoteroId;
    private String password;
    @ElementCollection(targetClass=SimpleGrantedAuthority.class, fetch=FetchType.EAGER)
    @Cascade({CascadeType.ALL})
    private Set<SimpleGrantedAuthority> roles;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    @Lob
    private String notes;
    
    public boolean isAdmin() {
        if (roles == null) {
            return false;
        }
        
        for (SimpleGrantedAuthority role : roles) {
            if (role.getAuthority().equals(Role.ADMIN)) {
                return true;
            }
        };
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#getFirstName()
     */
    @Override
    public String getFirstName() {
        return firstName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#setFirstName(java.lang.String)
     */
    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#getLastName()
     */
    @Override
    public String getLastName() {
        return lastName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#setLastName(java.lang.String)
     */
    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#getEmail()
     */
    @Override
    public String getEmail() {
        return email;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#getZoteroId()
     */
    @Override
    public String getZoteroId() {
        return zoteroId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#setZoteroId(java.lang.String)
     */
    @Override
    public void setZoteroId(String zoteroId) {
        this.zoteroId = zoteroId;
    }
    @Override
    public void setRoles(Set<SimpleGrantedAuthority> roles) {
        this.roles = roles;
    }
    @Override
    public Set<SimpleGrantedAuthority> getRoles() {
        return roles;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IUser#getPassword()
     */
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    @Override
    public void setAccountNonExpired(boolean accountNonExpired ) {
        this.accountNonExpired = accountNonExpired;
    }
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    @Override
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    @Override
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public String getNotes() {
        return notes;
    }
    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
}
