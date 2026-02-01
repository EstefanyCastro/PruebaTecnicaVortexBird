package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.CreateTicketPurchaseDTO;
import com.vortexbird.movieticket.dto.PaymentInfoDTO;
import com.vortexbird.movieticket.dto.TicketPurchaseDTO;
import com.vortexbird.movieticket.model.*;
import com.vortexbird.movieticket.repository.ITicketPurchaseRepository;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TicketPurchaseService.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mocks
 * - Act: Execute the method under test
 * - Assert: Verify the results
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TicketPurchaseService Tests")
class TicketPurchaseServiceTest {

    @Mock
    private ITicketPurchaseRepository purchaseRepository;

    @Mock
    private ICustomerService customerService;

    @Mock
    private IMovieService movieService;

    @InjectMocks
    private TicketPurchaseService purchaseService;

    private Customer customer;
    private Movie movie;
    private CreateTicketPurchaseDTO createPurchaseDTO;
    private TicketPurchase purchase;
    private PaymentInfoDTO paymentInfo;

    @BeforeEach
    void setUp() {
        // Arrange: Setup common test data
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("customer@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPhone("3001234567");
        customer.setRole(Role.CUSTOMER);
        customer.setIsEnabled(true);

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setImageUrl("http://example.com/image.jpg");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setPrice(15000.0);
        movie.setIsEnabled(true);

        paymentInfo = new PaymentInfoDTO();
        paymentInfo.setCardNumber("1234567890123456");
        paymentInfo.setCardHolderName("John Doe");
        paymentInfo.setExpiryDate("12/25");
        paymentInfo.setCvv("123");

        createPurchaseDTO = new CreateTicketPurchaseDTO();
        createPurchaseDTO.setMovieId(1L);
        createPurchaseDTO.setQuantity(2);
        createPurchaseDTO.setPaymentInfo(paymentInfo);

        purchase = new TicketPurchase();
        purchase.setId(1L);
        purchase.setCustomer(customer);
        purchase.setMovie(movie);
        purchase.setQuantity(2);
        purchase.setUnitPrice(15000.0);
        purchase.setTotalAmount(30000.0);
        purchase.setStatus(PurchaseStatus.CONFIRMED);
        purchase.setCardLastFour("3456");
        purchase.setCardHolderName("John Doe");
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setConfirmationCode("TKT-ABC12345");
    }

    @Test
    @DisplayName("Should create purchase successfully")
    void testCreatePurchase_Success() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(purchaseRepository.save(any(TicketPurchase.class))).thenReturn(purchase);

        // Act
        TicketPurchase result = purchaseService.createPurchase(1L, createPurchaseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(purchase.getId(), result.getId());
        assertEquals(customer.getId(), result.getCustomer().getId());
        assertEquals(movie.getId(), result.getMovie().getId());
        assertEquals(2, result.getQuantity());
        assertEquals(15000.0, result.getUnitPrice());
        assertEquals(30000.0, result.getTotalAmount());
        assertEquals(PurchaseStatus.CONFIRMED, result.getStatus());
        assertEquals("3456", result.getCardLastFour());
        assertNotNull(result.getConfirmationCode());
        verify(customerService).getCustomerById(1L);
        verify(movieService).getMovieById(1L);
        verify(purchaseRepository).save(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer not found")
    void testCreatePurchase_CustomerNotFound() {
        // Arrange
        when(customerService.getCustomerById(1L))
            .thenThrow(new ResourceNotFoundException("Customer not found with id: 1"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseService.createPurchase(1L, createPurchaseDTO);
        });

        verify(customerService).getCustomerById(1L);
        verify(movieService, never()).getMovieById(anyLong());
        verify(purchaseRepository, never()).save(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when movie not found")
    void testCreatePurchase_MovieNotFound() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(movieService.getMovieById(1L))
            .thenThrow(new ResourceNotFoundException("Movie not found with id: 1"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseService.createPurchase(1L, createPurchaseDTO);
        });

        verify(customerService).getCustomerById(1L);
        verify(movieService).getMovieById(1L);
        verify(purchaseRepository, never()).save(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("Should calculate total amount correctly")
    void testCreatePurchase_CalculatesTotalAmount() {
        // Arrange
        createPurchaseDTO.setQuantity(5);
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(purchaseRepository.save(any(TicketPurchase.class))).thenAnswer(invocation -> {
            TicketPurchase saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        TicketPurchase result = purchaseService.createPurchase(1L, createPurchaseDTO);

        // Assert
        assertEquals(15000.0, result.getUnitPrice());
        assertEquals(75000.0, result.getTotalAmount()); // 15000 * 5
        verify(purchaseRepository).save(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("Should store only last 4 digits of card")
    void testCreatePurchase_StoresCardLastFourDigits() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(purchaseRepository.save(any(TicketPurchase.class))).thenAnswer(invocation -> {
            TicketPurchase saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        TicketPurchase result = purchaseService.createPurchase(1L, createPurchaseDTO);

        // Assert
        assertEquals("3456", result.getCardLastFour());
        assertNotEquals(paymentInfo.getCardNumber(), result.getCardLastFour());
        verify(purchaseRepository).save(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("Should get purchase by id successfully")
    void testGetPurchaseById_Success() {
        // Arrange
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        // Act
        TicketPurchase result = purchaseService.getPurchaseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(purchase.getId(), result.getId());
        assertEquals(purchase.getConfirmationCode(), result.getConfirmationCode());
        verify(purchaseRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when purchase not found by id")
    void testGetPurchaseById_NotFound() {
        // Arrange
        when(purchaseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseService.getPurchaseById(1L);
        });

        verify(purchaseRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get customer purchases successfully")
    void testGetCustomerPurchases_Success() {
        // Arrange
        List<TicketPurchase> purchases = Arrays.asList(purchase);
        when(purchaseRepository.findByCustomerId(1L)).thenReturn(purchases);

        // Act
        List<TicketPurchaseDTO> result = purchaseService.getCustomerPurchases(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(purchase.getId(), result.get(0).getId());
        assertEquals(customer.getEmail(), result.get(0).getCustomerEmail());
        assertEquals(movie.getTitle(), result.get(0).getMovieTitle());
        verify(purchaseRepository).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should return empty list when customer has no purchases")
    void testGetCustomerPurchases_EmptyList() {
        // Arrange
        when(purchaseRepository.findByCustomerId(1L)).thenReturn(Arrays.asList());

        // Act
        List<TicketPurchaseDTO> result = purchaseService.getCustomerPurchases(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(purchaseRepository).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should get movie purchases successfully")
    void testGetMoviePurchases_Success() {
        // Arrange
        List<TicketPurchase> purchases = Arrays.asList(purchase);
        when(purchaseRepository.findByMovieId(1L)).thenReturn(purchases);

        // Act
        List<TicketPurchaseDTO> result = purchaseService.getMoviePurchases(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(purchase.getId(), result.get(0).getId());
        assertEquals(movie.getId(), result.get(0).getMovieId());
        verify(purchaseRepository).findByMovieId(1L);
    }

    @Test
    @DisplayName("Should return empty list when movie has no purchases")
    void testGetMoviePurchases_EmptyList() {
        // Arrange
        when(purchaseRepository.findByMovieId(1L)).thenReturn(Arrays.asList());

        // Act
        List<TicketPurchaseDTO> result = purchaseService.getMoviePurchases(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(purchaseRepository).findByMovieId(1L);
    }

    @Test
    @DisplayName("Should convert purchase to DTO correctly")
    void testToDTO_Success() {
        // Act
        TicketPurchaseDTO result = purchaseService.toDTO(purchase);

        // Assert
        assertNotNull(result);
        assertEquals(purchase.getId(), result.getId());
        assertEquals(customer.getId(), result.getCustomerId());
        assertEquals(customer.getEmail(), result.getCustomerEmail());
        assertEquals(customer.getFirstName() + " " + customer.getLastName(), result.getCustomerName());
        assertEquals(movie.getId(), result.getMovieId());
        assertEquals(movie.getTitle(), result.getMovieTitle());
        assertEquals(purchase.getQuantity(), result.getQuantity());
        assertEquals(purchase.getUnitPrice(), result.getUnitPrice());
        assertEquals(purchase.getTotalAmount(), result.getTotalAmount());
        assertEquals(purchase.getStatus(), result.getStatus());
        assertEquals(purchase.getCardLastFour(), result.getCardLastFour());
        assertEquals(purchase.getCardHolderName(), result.getCardHolderName());
        assertEquals(purchase.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(purchase.getConfirmationCode(), result.getConfirmationCode());
    }

    @Test
    @DisplayName("Should generate unique confirmation codes")
    void testCreatePurchase_GeneratesUniqueConfirmationCode() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(purchaseRepository.save(any(TicketPurchase.class))).thenAnswer(invocation -> {
            TicketPurchase saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        TicketPurchase result1 = purchaseService.createPurchase(1L, createPurchaseDTO);
        TicketPurchase result2 = purchaseService.createPurchase(1L, createPurchaseDTO);

        // Assert
        assertNotNull(result1.getConfirmationCode());
        assertNotNull(result2.getConfirmationCode());
        assertTrue(result1.getConfirmationCode().startsWith("TKT-"));
        assertTrue(result2.getConfirmationCode().startsWith("TKT-"));
        assertNotEquals(result1.getConfirmationCode(), result2.getConfirmationCode());
    }
}
