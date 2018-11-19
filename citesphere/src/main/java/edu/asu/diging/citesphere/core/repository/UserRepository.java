package edu.asu.diging.citesphere.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.impl.User;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

}
