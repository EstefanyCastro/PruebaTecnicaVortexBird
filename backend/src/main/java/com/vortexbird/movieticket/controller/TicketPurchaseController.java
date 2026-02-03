package com.vortexbird.movieticket.controller;

import com.vortexbird.movieticket.dto.CreateTicketPurchaseDTO;
import com.vortexbird.movieticket.dto.TicketPurchaseDTO;
import com.vortexbird.movieticket.model.TicketPurchase;
import com.vortexbird.movieticket.service.ITicketPurchaseService;
import com.vortexbird.movieticket.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for TicketPurchase management.
 *
 * Handles HTTP requests for ticket purchase operations.
 */
@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@Slf4j
public class TicketPurchaseController {

    private final ITicketPurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketPurchaseDTO>> createPurchase(
            @Valid @RequestBody CreateTicketPurchaseDTO dto,
            @RequestParam Long customerId) {
        log.info("POST /purchases - Creating purchase for customer: {}", customerId);
        TicketPurchase purchase = purchaseService.createPurchase(customerId, dto);
        TicketPurchaseDTO purchaseDTO = purchaseService.toDTO(purchase);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(purchaseDTO, "Purchase created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketPurchaseDTO>> getPurchase(@PathVariable Long id) {
        log.info("GET /purchases/{} - Fetching purchase", id);
        TicketPurchase purchase = purchaseService.getPurchaseById(id);
        TicketPurchaseDTO purchaseDTO = purchaseService.toDTO(purchase);
        return ResponseEntity.ok(ApiResponse.success(purchaseDTO, "Purchase retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<TicketPurchaseDTO>>> getCustomerPurchases(
            @PathVariable Long customerId) {
        log.info("GET /purchases/customer/{} - Fetching customer purchases", customerId);
        List<TicketPurchaseDTO> purchases = purchaseService.getCustomerPurchases(customerId);
        return ResponseEntity.ok(ApiResponse.success(purchases, "Customer purchases retrieved successfully"));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<TicketPurchaseDTO>>> getMoviePurchases(
            @PathVariable Long movieId) {
        log.info("GET /purchases/movie/{} - Fetching movie purchases (admin)", movieId);
        List<TicketPurchaseDTO> purchases = purchaseService.getMoviePurchases(movieId);
        return ResponseEntity.ok(ApiResponse.success(purchases, "Movie purchases retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelPurchase(@PathVariable Long id) {
        log.info("DELETE /purchases/{} - Cancelling purchase", id);
        purchaseService.cancelPurchase(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase cancelled successfully"));
    }
}
