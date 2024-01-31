package edu.asu.diging.citesphere.core.model.oauth;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

public interface IOAuthClient {

    void setAutoApprove(boolean autoApprove);

    void setRefereshTokenValiditySeconds(int refereshTokenValiditySeconds);

    void setAccessTokenValiditySeconds(int accessTokenValiditySeconds);

    void setAuthorities(Collection<GrantedAuthority> authorities);

    void setRegisteredRedirectUri(Set<String> registeredRedirectUri);

    void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes);

    void setScope(Set<String> scope);

    void setScoped(boolean scoped);

    void setClientSecret(String clientSecret);

    void setSecretRequired(boolean secretRequired);

    void setResourceIds(Set<String> resourceIds);

    void setClientId(String clientId);

    Map<String, Object> getAdditionalInformation();

    boolean isAutoApprove(String scope);

    Integer getRefreshTokenValiditySeconds();

    Integer getAccessTokenValiditySeconds();

    Collection<GrantedAuthority> getAuthorities();

    Set<String> getRegisteredRedirectUri();

    Set<String> getAuthorizedGrantTypes();

    Set<String> getScope();

    boolean isScoped();

    String getClientSecret();

    boolean isSecretRequired();

    Set<String> getResourceIds();

    String getClientId();

    void setName(String name);

    String getName();

    void setDescription(String description);

    String getDescription();
    
    boolean getIsUserAccessToken();
    
    void setisUserAccessToken(boolean isUserAccessToken);
}