package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessToken;
import edu.asu.diging.citesphere.core.repository.PersonalAccessTokenRepository;
import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenManager;
import edu.asu.diging.citesphere.core.user.IUserManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class PersonalAccessTokenManager implements IPersonalAccessTokenManager {

    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private IUserManager userManager;
    
    @Autowired
    public PersonalAccessTokenManager(PersonalAccessTokenRepository personalAccessTokenRepository,
            IUserManager userManager) {
        this.personalAccessTokenRepository = personalAccessTokenRepository;
        this.userManager = userManager;
    }

    @Override
    public String savePersonalAccessToken(String username) {
        PersonalAccessToken tokenObj = new PersonalAccessToken();
        tokenObj.setUsername(username);
        String randomId = UUID.randomUUID().toString();
        String tokenGenerated = bCryptPasswordEncoder.encode(randomId);
        tokenObj.setToken(tokenGenerated);
        tokenObj.setCreatedOn(OffsetDateTime.now());
        personalAccessTokenRepository.save(tokenObj);
        return randomId;
    }

    @Override
    public List<IPersonalAccessToken> getPersonalAccessTokens(String username) {
        return personalAccessTokenRepository.findByUsername(username);
    }
    
    @Override
    public Optional<Authentication> verifyToken(Optional<String> token) {
        if (!token.isPresent()) {
            return Optional.empty();
        }

        String decodedTokenString = new String(Base64.getDecoder().decode(token.get()));
        if(decodedTokenString.contains(":")) {
            String userName = decodedTokenString.split(":")[0];
            String password = decodedTokenString.split(":")[1];
    
            List<IPersonalAccessToken> personalAccessTokensByUsername = personalAccessTokenRepository
                    .findByUsername(userName);
    
            // String encodedTokenString = Base64.getEncoder().encodeToString(password.getBytes());
    
            for (IPersonalAccessToken tokenItem : personalAccessTokensByUsername) {
                if (bCryptPasswordEncoder.matches(password, tokenItem.getToken())){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
                            password, userManager.findByUsername(userName).getRoles());
                    return Optional.of(authentication);
                }
            } 
        }
        // TODO: throw an exception that tells the user that the credentials are in the wrong form/invalid.
        return Optional.empty();
    }

}
