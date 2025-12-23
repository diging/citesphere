 citesphere/src/main/java/edu/asu/diging/citesphere/core/model/oauth/IOAuthClient.javapackage edu.asu.diging.citesphere.core.repository.oauth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessTokenOAuthClient;

/**
 * Repository for personal access token OAuth clients.
 * This repository handles the specialized PAT client subclass.
 */
public interface PersonalAccessTokenOAuthClientRepository
        extends JpaRepository<PersonalAccessTokenOAuthClient, String> {

    /**
     * Find a personal access token client by the username of the user who created it.
     * Each user has at most one PAT client.
     */
    Optional<PersonalAccessTokenOAuthClient> findByCreatedByUsername(String username);
}
