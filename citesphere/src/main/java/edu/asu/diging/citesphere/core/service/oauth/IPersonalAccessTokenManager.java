package edu.asu.diging.citesphere.core.service.oauth;

import org.springframework.data.domain.Pageable;

import edu.asu.diging.citesphere.core.exceptions.CannotFindTokenException;
import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.user.IUser;

/**
 * Manager for personal access token operations.
 * Personal access tokens allow users to authenticate against the API
 * without going through the OAuth authorization code flow.
 */
public interface IPersonalAccessTokenManager {

    /**
     * Creates a new personal access token for the user.
     *
     * @param name the user-given name for the token
     * @param user the user creating the token
     * @return credentials containing the token ID and token value (shown only once)
     */
    PersonalAccessTokenCredentials createToken(String name, IUser user);

    /**
     * Gets all personal access tokens for the user.
     *
     * @param user the user whose tokens to retrieve
     * @param pageable pagination information
     * @return paginated result of tokens
     */
    PersonalAccessTokenResultPage getTokensForUser(IUser user, Pageable pageable);

    /**
     * Deletes a personal access token.
     *
     * @param tokenId the ID of the token to delete
     * @param user the user who owns the token
     * @throws CannotFindTokenException if the token is not found or doesn't belong to the user
     */
    void deleteToken(String tokenId, IUser user) throws CannotFindTokenException;

    /**
     * Regenerates a personal access token, invalidating the old token value
     * and creating a new one.
     *
     * @param tokenId the ID of the token to regenerate
     * @param user the user who owns the token
     * @return credentials containing the token ID and new token value
     * @throws CannotFindTokenException if the token is not found or doesn't belong to the user
     */
    PersonalAccessTokenCredentials regenerateToken(String tokenId, IUser user) throws CannotFindTokenException;

    /**
     * Gets a personal access token by ID.
     *
     * @param tokenId the ID of the token
     * @return the token, or null if not found
     */
    IPersonalAccessToken getTokenById(String tokenId);
}
