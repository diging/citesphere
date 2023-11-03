package edu.asu.diging.citesphere.core.repository.oauth;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.UserAccessToken;

@JaversSpringDataAuditable
public interface UserAccessTokenRepository extends JpaRepository<UserAccessToken, String> {

}
