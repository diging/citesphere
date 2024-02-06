package edu.asu.diging.citesphere.core.repository.oauth;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {
    Page<OAuthClient> findByIsUserAccessTokenAndCreatedByUsername(boolean isUserAccessToken, String createdByUsername, Pageable pageable);
}
