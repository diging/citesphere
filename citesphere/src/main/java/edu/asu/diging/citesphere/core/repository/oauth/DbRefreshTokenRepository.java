package edu.asu.diging.citesphere.core.repository.oauth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.DbRefreshToken;

/**
 * Modeled after:
 * https://blog.couchbase.com/custom-token-store-spring-securtiy-oauth2/
 * 
 * @author jdamerow
 *
 */
public interface DbRefreshTokenRepository extends JpaRepository<DbRefreshToken, String> {

    Optional<DbRefreshToken> findByTokenId(String tokenId);

    Optional<DbRefreshToken> findByAuthentication(String authentication);

}