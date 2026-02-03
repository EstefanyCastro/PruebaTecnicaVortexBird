package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.CreateTicketPurchaseDTO;
import com.vortexbird.movieticket.dto.TicketPurchaseDTO;
import com.vortexbird.movieticket.model.Customer;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.model.PurchaseStatus;
import com.vortexbird.movieticket.model.TicketPurchase;
import com.vortexbird.movieticket.repository.ITicketPurchaseRepository;
import com.vortexbird.movieticket.shared.exception.BusinessException;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of TicketPurchase Service.
 *
 * Contains business logic for ticket purchase management.
 * Implements the Service pattern for clean architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketPurchaseService implements ITicketPurchaseService {

    private final ITicketPurchaseRepository purchaseRepository;
    private final ICustomerService customerService;
    private final IMovieService movieService;
    private final IEmailService emailService;

    @Override
    public TicketPurchase createPurchase(Long customerId, CreateTicketPurchaseDTO dto) {
        log.info("Creating purchase for customer: {} and movie: {}", customerId, dto.getMovieId());
        
        Customer customer = customerService.getCustomerById(customerId);
        Movie movie = movieService.getMovieById(dto.getMovieId());
        
        // Calculate amounts
        Double unitPrice = movie.getPrice();
        Double totalAmount = unitPrice * dto.getQuantity();
        
        // Create purchase entity
        TicketPurchase purchase = new TicketPurchase();
        purchase.setCustomer(customer);
        purchase.setMovie(movie);
        purchase.setQuantity(dto.getQuantity());
        purchase.setUnitPrice(unitPrice);
        purchase.setTotalAmount(totalAmount);
        purchase.setStatus(PurchaseStatus.CONFIRMED);
        
        // Store only last 4 digits of card for security
        String cardNumber = dto.getPaymentInfo().getCardNumber();
        purchase.setCardLastFour(cardNumber.substring(cardNumber.length() - 4));
        purchase.setCardHolderName(dto.getPaymentInfo().getCardHolderName());
        
        // Generate confirmation code
        purchase.setConfirmationCode(generateConfirmationCode());
        
        TicketPurchase savedPurchase = purchaseRepository.save(purchase);
        log.info("Purchase created successfully with confirmation code: {}", savedPurchase.getConfirmationCode());
        
        // Send confirmation email asynchronously
        log.info("Attempting to send confirmation email to: {}", savedPurchase.getCustomer().getEmail());
        emailService.sendPurchaseConfirmation(savedPurchase);
        log.info("Email service called for confirmation code: {}", savedPurchase.getConfirmationCode());
        
        return savedPurchase;
    }

    @Override
    @Transactional(readOnly = true)
    public TicketPurchase getPurchaseById(Long id) {
        log.info("Fetching purchase with id: {}", id);
        return purchaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketPurchaseDTO> getCustomerPurchases(Long customerId) {
        log.info("Fetching purchases for customer: {}", customerId);
        return purchaseRepository.findByCustomerId(customerId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketPurchaseDTO> getMoviePurchases(Long movieId) {
        log.info("Fetching purchases for movie: {}", movieId);
        return purchaseRepository.findByMovieId(movieId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelPurchase(Long id) {
        log.info("Cancelling purchase with id: {}", id);
        TicketPurchase purchase = getPurchaseById(id);
        
        if (purchase.getStatus() != PurchaseStatus.CONFIRMED) {
            throw new BusinessException("Only confirmed purchases can be cancelled");
        }
        
        purchase.setStatus(PurchaseStatus.CANCELLED);
        purchaseRepository.save(purchase);
        log.info("Purchase cancelled successfully: {}", id);
    }

    @Override
    public TicketPurchaseDTO toDTO(TicketPurchase purchase) {
        TicketPurchaseDTO dto = new TicketPurchaseDTO();
        dto.setId(purchase.getId());
        dto.setCustomerId(purchase.getCustomer().getId());
        dto.setCustomerEmail(purchase.getCustomer().getEmail());
        dto.setCustomerName(purchase.getCustomer().getFirstName() + " " + purchase.getCustomer().getLastName());
        dto.setMovieId(purchase.getMovie().getId());
        dto.setMovieTitle(purchase.getMovie().getTitle());
        dto.setQuantity(purchase.getQuantity());
        dto.setUnitPrice(purchase.getUnitPrice());
        dto.setTotalAmount(purchase.getTotalAmount());
        dto.setStatus(purchase.getStatus());
        dto.setCardLastFour(purchase.getCardLastFour());
        dto.setCardHolderName(purchase.getCardHolderName());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setConfirmationCode(purchase.getConfirmationCode());
        return dto;
    }

    /**
     * Generate a unique confirmation code for the purchase.
     */
    private String generateConfirmationCode() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
