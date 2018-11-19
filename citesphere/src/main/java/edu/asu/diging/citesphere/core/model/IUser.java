package edu.asu.diging.citesphere.core.model;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface IUser {

    String getUsername();

    void setUsername(String username);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmail();

    void setEmail(String email);

    String getZoteroId();

    void setZoteroId(String zoteroId);

    String getPassword();

    void setEnabled(boolean enabled);

    void setPassword(String password);

    void setNotes(String notes);

    String getNotes();

    void setCredentialsNonExpired(boolean credentialsNonExpired);

    void setAccountNonLocked(boolean accountNonLocked);

    void setAccountNonExpired(boolean accountNonExpired);

    void setRoles(Set<SimpleGrantedAuthority> roles);

    Set<SimpleGrantedAuthority> getRoles();

}