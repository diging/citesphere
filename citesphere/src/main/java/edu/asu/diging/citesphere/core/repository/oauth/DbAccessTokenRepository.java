package edu.asu.diging.citesphere.core.repository.oauth;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;

/**
 * Modeled after:
 * https://blog.couchbase.com/custom-token-store-spring-securtiy-oauth2/
 * @author jdamerow
 *
 */
public interface DbAccessTokenRepository extends JpaRepository<DbAccessToken, String> {
 
    List<DbAccessToken> findByClientId(String clientId);
 
    List<DbAccessToken> findByClientIdAndUsername(String clientId, String username);
    
    List<DbAccessToken> findByUsername(String username);
 
    Optional<DbAccessToken> findByTokenId(String tokenId);
 
    Optional<DbAccessToken> findByRefreshToken(String refreshToken);
 
    Optional<DbAccessToken> findByAuthenticationId(String authenticationId);
   
    @Transactional
    List<DbAccessToken> deleteByClientId(String clientId);
 
}