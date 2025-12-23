package edu.asu.diging.citesphere.core.model.oauth;

import java.time.OffsetDateTime;

/**
 * Interface representing a personal access token for API access.
 * Personal access tokens allow users to authenticate against the API
 * without going through the OAuth authorization code flow.
 */
public interface IPersonalAccessToken {

    String getId();

    String getName();
    void setName(String name);

    String getUsername();

    OffsetDateTime getCreatedAt();
    void setCreatedAt(OffsetDateTime createdAt);

    boolean isPersonalAccessToken();
    void setPersonalAccessToken(boolean isPersonalAccessToken);
}
