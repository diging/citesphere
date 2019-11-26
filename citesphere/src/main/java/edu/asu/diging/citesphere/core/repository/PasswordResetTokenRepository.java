package edu.asu.diging.citesphere.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.impl.PasswordResetToken;

public interface PasswordResetTokenRepository extends PagingAndSortingRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
}
