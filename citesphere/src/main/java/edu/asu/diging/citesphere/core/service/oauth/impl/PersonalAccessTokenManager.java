package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.javers.common.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.CannotFindTokenException;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessTokenOAuthClient;
import edu.asu.diging.citesphere.core.repository.oauth.DbAccessTokenRepository;
import edu.asu.diging.citesphere.core.repository.oauth.PersonalAccessTokenOAuthClientRepository;
import edu.asu.diging.citesphere.core.service.oauth.IPersonalAccessTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;
import edu.asu.diging.citesphere.core.service.oauth.PersonalAccessTokenCredentials;
import edu.asu.diging.citesphere.core.service.oauth.PersonalAccessTokenResultPage;
import edu.asu.diging.citesphere.user.IUser;

/**
 * Manager for personal access token operations.
 * This class handles the creation, retrieval, and management of personal access tokens,
 * which allow users to authenticate against the API without going through the OAuth
 * authorization code flow.
 */
@Service
@Transactional
public class PersonalAccessTokenManager implements IPersonalAccessTokenManager {

    @Autowired
    private PersonalAccessTokenOAuthClientRepository patClientRepo;

    @Autowired
    private DbAccessTokenRepository accessTokenRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public PersonalAccessTokenCredentials createToken(String name, IUser user) {
        PersonalAccessTokenOAuthClient patClient = getOrCreatePATClientForUser(user);

        OAuth2AccessToken accessToken = createAccessToken();

        DbAccessToken dbToken = new DbAccessToken();
        dbToken.setId(UUID.randomUUID().toString() + UUID.randomUUID().toString());
        dbToken.setTokenId(extractTokenKey(accessToken.getValue()));
        dbToken.setToken(accessToken);
        dbToken.setUsername(user.getUsername());
        dbToken.setClientId(patClient.getClientId());
        dbToken.setName(name);
        dbToken.setCreatedAt(OffsetDateTime.now());
        dbToken.setPersonalAccessToken(true);

        accessTokenRepo.save(dbToken);

        return new PersonalAccessTokenCredentials(dbToken.getId(), accessToken.getValue(), name);
    }

    @Override
    public PersonalAccessTokenResultPage getTokensForUser(IUser user, Pageable pageable) {
        Page<DbAccessToken> tokensPage = accessTokenRepo.findByUsernameAndPersonalAccessToken(
                user.getUsername(), true, pageable);

        List<IPersonalAccessToken> tokens = new ArrayList<>();
        tokensPage.forEach(token -> tokens.add(token));

        PersonalAccessTokenResultPage result = new PersonalAccessTokenResultPage();
        result.setTokens(tokens);
        result.setTotalPages(tokensPage.getTotalPages());
        return result;
    }

    @Override
    public void deleteToken(String tokenId, IUser user) throws CannotFindTokenException {
        Optional<DbAccessToken> tokenOptional = accessTokenRepo.findByIdAndUsername(tokenId, user.getUsername());
        if (!tokenOptional.isPresent()) {
            throw new CannotFindTokenException("Token with id " + tokenId + " does not exist or does not belong to user.");
        }

        DbAccessToken token = tokenOptional.get();
        if (!token.isPersonalAccessToken()) {
            throw new CannotFindTokenException("Token with id " + tokenId + " is not a personal access token.");
        }

        accessTokenRepo.delete(token);
    }

    @Override
    public PersonalAccessTokenCredentials regenerateToken(String tokenId, IUser user) throws CannotFindTokenException {
        Optional<DbAccessToken> tokenOptional = accessTokenRepo.findByIdAndUsername(tokenId, user.getUsername());
        if (!tokenOptional.isPresent()) {
            throw new CannotFindTokenException("Token with id " + tokenId + " does not exist or does not belong to user.");
        }

        DbAccessToken oldToken = tokenOptional.get();
        if (!oldToken.isPersonalAccessToken()) {
            throw new CannotFindTokenException("Token with id " + tokenId + " is not a personal access token.");
        }

        String tokenName = oldToken.getName();

        accessTokenRepo.delete(oldToken);

        PersonalAccessTokenOAuthClient patClient = getOrCreatePATClientForUser(user);
        OAuth2AccessToken newAccessToken = createAccessToken();

        DbAccessToken newDbToken = new DbAccessToken();
        newDbToken.setId(UUID.randomUUID().toString() + UUID.randomUUID().toString());
        newDbToken.setTokenId(extractTokenKey(newAccessToken.getValue()));
        newDbToken.setToken(newAccessToken);
        newDbToken.setUsername(user.getUsername());
        newDbToken.setClientId(patClient.getClientId());
        newDbToken.setName(tokenName);
        newDbToken.setCreatedAt(OffsetDateTime.now());
        newDbToken.setPersonalAccessToken(true);

        accessTokenRepo.save(newDbToken);

        return new PersonalAccessTokenCredentials(newDbToken.getId(), newAccessToken.getValue(), tokenName);
    }

    @Override
    public IPersonalAccessToken getTokenById(String tokenId) {
        Optional<DbAccessToken> tokenOptional = accessTokenRepo.findById(tokenId);
        if (tokenOptional.isPresent() && tokenOptional.get().isPersonalAccessToken()) {
            return tokenOptional.get();
        }
        return null;
    }

    /**
     * Gets or creates the personal access token OAuthClient for a user.
     * Each user has exactly one PersonalAccessTokenOAuthClient that is used for all their personal access tokens.
     */
    private PersonalAccessTokenOAuthClient getOrCreatePATClientForUser(IUser user) {
        Optional<PersonalAccessTokenOAuthClient> existingClient = patClientRepo.findByCreatedByUsername(
                user.getUsername());

        if (existingClient.isPresent()) {
            return existingClient.get();
        }

        PersonalAccessTokenOAuthClient client = new PersonalAccessTokenOAuthClient();
        client.setName("Personal Access Token Client for " + user.getUsername());
        String clientSecret = UUID.randomUUID().toString();
        client.setClientSecret(bCryptPasswordEncoder.encode(clientSecret));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.TRUSTED_CLIENT));
        client.setAuthorities(authorities);

        client.setScope(new HashSet<>());
        client.getScope().add(OAuthScope.READ.getScope());

        client.setCreatedBy(user);

        return patClientRepo.save(client);
    }

    private OAuth2AccessToken createAccessToken() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        token.setScope(Sets.asSet(OAuthScope.READ.getScope()));
        return token;
    }


    private String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available. Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available. Fatal (should be in the JDK).");
        }
    }
}
