package edu.asu.diging.citesphere.core.service.impl;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.repository.PersonalAccessTokenRepository;
import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenService;
import edu.asu.diging.citesphere.core.user.IUserManager;

@Service
public class PersonalAccessTokenService implements IPersonalAccessTokenService {

    private final PersonalAccessTokenRepository personalAccessTokenRepository;

    private IUserManager userManager;

    @Autowired
    public PersonalAccessTokenService(PersonalAccessTokenRepository personalAccessTokenRepository,
            IUserManager userManager) {
        this.personalAccessTokenRepository = personalAccessTokenRepository;
        this.userManager = userManager;
    }

    @Override
    public Optional<Authentication> verifyToken(Optional<String> token) {
        if (!token.isPresent()) {
            return Optional.empty();
        }

        byte[] decodedTokenBytes = Base64.getDecoder().decode(token.get());
        String decodedTokenString = new String(decodedTokenBytes);

        String userName = decodedTokenString.split(":")[0];
        String password = decodedTokenString.split(":")[1];

        List<IPersonalAccessToken> personalAccessTokensByUsername = personalAccessTokenRepository
                .findByUsername(userName);

        String encodedTokenString = Base64.getEncoder().encodeToString(password.getBytes());

        for (IPersonalAccessToken tokenItem : personalAccessTokensByUsername) {
            if (tokenItem.getToken().equals(encodedTokenString)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
                        password, userManager.findByUsername(userName).getRoles());
                return Optional.of(authentication);
            }
        }

        return Optional.empty();
    }

}