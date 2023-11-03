package edu.asu.diging.citesphere.core.model.oauth.impl;

import java.util.Collection;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.security.core.GrantedAuthority;

import edu.asu.diging.citesphere.core.model.oauth.IUserAccessToken;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

@Entity
public class UserAccessToken implements IUserAccessToken {
    
    @Id
    @GeneratedValue(generator = "access_token_id_generator")
    @GenericGenerator(name = "access_token_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "AT"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
    )
    private String clientId;
    
    private String token;
    
    private String name;
    
    @ElementCollection
    private Collection<GrantedAuthority> authorities;
    
    @ElementCollection
    private Set<String> scope;
    
    @ManyToOne(targetEntity=User.class)
    private IUser user;
    
    @Override
    public String getToken() {
        return token;
    }
    
    @Override
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public IUser getUser() {
        return user;
    }
    
    @Override
    public void setUser(IUser user) {
        this.user = user;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getClientId() {
        return clientId;
    }
    
    @Override
    public Set<String> getScope() {
        return scope;
    }
    
    @Override
    public void setScope(Set<String> scope) {
        this.scope = scope;
    }
    
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
