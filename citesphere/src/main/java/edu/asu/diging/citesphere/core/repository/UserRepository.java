package edu.asu.diging.citesphere.core.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.model.impl.User;

@JaversSpringDataAuditable
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    public List<User> findByEmail(String email);
}
