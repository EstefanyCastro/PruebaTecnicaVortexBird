package com.vortexbird.movieticket.repository;

import com.vortexbird.movieticket.model.TicketPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TicketPurchase entity.
 *
 * Provides database access operations for TicketPurchase entities using Spring Data JPA.
 */
@Repository
public interface ITicketPurchaseRepository extends JpaRepository<TicketPurchase, Long> {

    List<TicketPurchase> findByCustomerId(Long customerId);

    List<TicketPurchase> findByMovieId(Long movieId);

    Optional<TicketPurchase> findByConfirmationCode(String confirmationCode);
}
