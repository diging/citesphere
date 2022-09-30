package edu.asu.diging.citesphere.core.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;

public interface IPersonalAccessTokenService {

    public Optional<Authentication> verifyToken(Optional<String> token);
}
