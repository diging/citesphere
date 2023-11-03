package edu.asu.diging.citesphere.core.model.oauth;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import edu.asu.diging.citesphere.user.IUser;

public interface IUserAccessToken {

    String getToken();

    void setToken(String token);

    IUser getUser();

    void setUser(IUser user);
    
    String getName();
    
    void setName(String name);
    
    String getClientId();

    Set<String> getScope();

    void setScope(Set<String> scope);

    Collection<GrantedAuthority> getAuthorities();

    void setAuthorities(Collection<GrantedAuthority> authorities);
}
