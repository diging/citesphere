package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessToken;
import edu.asu.diging.citesphere.core.repository.PersonalAccessTokenRepository;
import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenManager;

@Service
public class PersonalAccessTokenManager implements IPersonalAccessTokenManager {

    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    @Override
    public String savePersonalAccessToken(String username) {
        PersonalAccessToken tokenObj = new PersonalAccessToken();
        tokenObj.setUsername(username);
        String tokenGenerated = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        tokenObj.setToken(tokenGenerated);
        tokenObj.setCreatedOn(OffsetDateTime.now());
        tokenObj.setId(UUID.randomUUID().toString() + UUID.randomUUID().toString());
        personalAccessTokenRepository.save(tokenObj);
        return tokenGenerated;
    }

    @Override
    public List<IPersonalAccessToken> getPersonalAccessTokens(String username) {
        return personalAccessTokenRepository.findByUsername(username);
    }

}
