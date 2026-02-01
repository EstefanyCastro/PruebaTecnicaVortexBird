package com.vortexbird.movieticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TicketPurchase entity representing a purchase transaction.
 *
 * Links a customer with a movie purchase, including quantity and payment details.
 * Follows JPA entity pattern for ORM mapping.
 */
@Entity
@Table(name = "ticket_purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPurchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseStatus status;
    
    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;
    
    @Column(name = "card_holder_name", length = 200)
    private String cardHolderName;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
    
    @Column(name = "confirmation_code", unique = true, length = 50)
    private String confirmationCode;

    @PrePersist
    protected void onCreate() {
        purchaseDate = LocalDateTime.now();
        if (status == null) {
            status = PurchaseStatus.PENDING;
        }
    }
}
