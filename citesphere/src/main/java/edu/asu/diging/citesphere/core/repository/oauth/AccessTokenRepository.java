package edu.asu.diging.citesphere.core.repository.oauth;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.user.impl.AccessToken;

@JaversSpringDataAuditable
public interface AccessTokenRepository extends PagingAndSortingRepository<AccessToken, String> {

}
