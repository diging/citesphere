package edu.asu.diging.citesphere.core.model.oauth.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Subclass of OAuthClient specifically for personal access tokens.
 * The type itself identifies PAT clients - no additional fields needed.
 * JPA uses the discriminator column (DTYPE) to distinguish this type from regular OAuthClients.
 */
@Entity
@DiscriminatorValue("PAT")
public class PersonalAccessTokenOAuthClient extends OAuthClient {

    private static final long serialVersionUID = 1L;
}
