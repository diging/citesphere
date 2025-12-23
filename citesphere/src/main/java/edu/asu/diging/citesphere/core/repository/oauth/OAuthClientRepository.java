package edu.asu.diging.citesphere.core.repository.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;

/**
 * Repository for OAuth clients.
 * Note: Personal access token clients now have their own repository
 * (PersonalAccessTokenOAuthClientRepository).
 */
public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {

}
