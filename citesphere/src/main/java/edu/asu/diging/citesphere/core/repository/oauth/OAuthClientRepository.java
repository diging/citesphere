package edu.asu.diging.citesphere.core.repository.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {

}
