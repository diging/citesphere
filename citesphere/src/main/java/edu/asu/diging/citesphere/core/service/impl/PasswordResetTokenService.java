package edu.asu.diging.citesphere.core.service.impl;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.email.IEmailNotificationManager;
import edu.asu.diging.citesphere.core.exceptions.TokenExpiredException;
import edu.asu.diging.citesphere.core.exceptions.UserDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IPasswordResetToken;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.impl.PasswordResetToken;
import edu.asu.diging.citesphere.core.repository.PasswordResetTokenRepository;
import edu.asu.diging.citesphere.core.service.IPasswordResetTokenService;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.IUser;

@Service
public class PasswordResetTokenService implements IPasswordResetTokenService {

    @Autowired
    @Qualifier(value = "appFile")
    private Properties appProperties;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private IEmailNotificationManager emailService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.simpleusers.core.service.impl.ITokenService#resetPassword(java
     * .lang.String)
     */
    @Override
    public void resetPassword(String email) throws UserDoesNotExistException, IOException {
        IUser user = userManager.findByEmail(email);
        if (user == null) {
            throw new UserDoesNotExistException("User with email " + email + " does not exist.");
        }
        IPasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        OffsetDateTime currentTime = OffsetDateTime.now();
        OffsetDateTime expirationTime = currentTime
                .plusMinutes(new Integer(appProperties.getProperty("password.reset.expiration")));
        token.setExpiryDate(expirationTime);
        tokenRepo.save((PasswordResetToken) token);

        emailService.sendResetPasswordEmail(user, token);
    }

    @Override
    public boolean validateToken(String token, String username) throws InvalidTokenException, TokenExpiredException {
        PasswordResetToken resetToken = tokenRepo.findByToken(token);
        if (resetToken == null || !resetToken.getUser().getUsername().equals(username)) {
            throw new InvalidTokenException("Token is invalid.");
        }

        if (resetToken.getExpiryDate().isBefore(OffsetDateTime.now())) {
            throw new TokenExpiredException("Token expired on " + resetToken.getExpiryDate().toString());
        }

        IUser user = resetToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
                Arrays.asList(new SimpleGrantedAuthority(Role.CHANGE_PASSWORD)));
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // expire token so it can't be used again
        resetToken.setExpiryDate(OffsetDateTime.now());
        tokenRepo.save(resetToken);
        return true;
    }
}