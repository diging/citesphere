package edu.asu.diging.citesphere.core.service.impl;

import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessToken;
import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenManager;
import edu.asu.diging.citesphere.core.repository.PersonalAccessTokenRepository;
import java.time.OffsetDateTime;

@Service
public class PersonalAccessTokenManager implements IPersonalAccessTokenManager {

    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    @Override
    public void savePersonalAccessToken(String username) {
        PersonalAccessToken tokenObj = new PersonalAccessToken();
        tokenObj.setUsername(username);
        tokenObj.setToken(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()));
        tokenObj.setCreatedOn(OffsetDateTime.now());
        tokenObj.setId(UUID.randomUUID().toString() + UUID.randomUUID().toString());
        personalAccessTokenRepository.save(tokenObj);
    }
}
