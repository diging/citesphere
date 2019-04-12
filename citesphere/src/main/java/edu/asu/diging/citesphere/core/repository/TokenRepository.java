package edu.asu.diging.citesphere.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.impl.ZoteroToken;

public interface TokenRepository extends PagingAndSortingRepository<ZoteroToken, String> {

    public IZoteroToken findByUserUsername(String userId);
}
