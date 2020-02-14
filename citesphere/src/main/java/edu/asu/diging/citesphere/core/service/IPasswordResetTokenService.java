package edu.asu.diging.citesphere.core.service;

import java.io.IOException;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

import edu.asu.diging.citesphere.core.exceptions.TokenExpiredException;
import edu.asu.diging.citesphere.core.exceptions.UserDoesNotExistException;

public interface IPasswordResetTokenService {

    void resetPassword(String email) throws UserDoesNotExistException, IOException;

    boolean validateToken(String token, String username) throws InvalidTokenException, TokenExpiredException;

}