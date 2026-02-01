package com.vortexbird.movieticket.repository;

import com.vortexbird.movieticket.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity.
 *
 * Provides database access operations for Customer entities using Spring Data
 * JPA.
 */
@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByIdAndIsEnabledTrue(Long id);

    List<Customer> findByIsEnabledTrue();
    
    boolean existsByEmail(String email);
}
