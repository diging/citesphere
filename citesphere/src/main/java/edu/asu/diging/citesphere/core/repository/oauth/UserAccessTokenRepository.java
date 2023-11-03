package edu.asu.diging.citesphere.core.repository.oauth;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.oauth.impl.UserAccessToken;
import edu.asu.diging.citesphere.user.IUser;

@JaversSpringDataAuditable
public interface UserAccessTokenRepository extends JpaRepository<UserAccessToken, String> {
    Page<UserAccessToken> findAllByUser(IUser user, Pageable pageable);
    List<UserAccessToken> findAllByUser(IUser user);
}
