package edu.asu.diging.citesphere.core.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessToken;

@Repository
public interface PersonalAccessTokenRepository extends PagingAndSortingRepository<PersonalAccessToken, String> {

    public List<IPersonalAccessToken> findByUsername(String username);
}
