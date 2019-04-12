package edu.asu.diging.citesphere.core.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;
import edu.asu.diging.citesphere.core.model.impl.User;

@JaversSpringDataAuditable
public interface UserRepository extends PagingAndSortingRepository<User, String> {

}
