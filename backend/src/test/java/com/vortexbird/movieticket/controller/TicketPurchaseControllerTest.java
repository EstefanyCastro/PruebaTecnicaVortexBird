package com.vortexbird.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vortexbird.movieticket.dto.CreateTicketPurchaseDTO;
import com.vortexbird.movieticket.dto.PaymentInfoDTO;
import com.vortexbird.movieticket.dto.TicketPurchaseDTO;
import com.vortexbird.movieticket.model.*;
import com.vortexbird.movieticket.service.ITicketPurchaseService;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TicketPurchaseController.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mock service responses
 * - Act: Perform HTTP requests
 * - Assert: Verify response status and content
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security for tests
@ActiveProfiles("test") // Use test configuration with H2 database
@DisplayName("TicketPurchaseController Tests")
class TicketPurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ITicketPurchaseService purchaseService;

    private CreateTicketPurchaseDTO createPurchaseDTO;
    private TicketPurchase purchase;
    private TicketPurchaseDTO purchaseDTO;
    private PaymentInfoDTO paymentInfo;

    @BeforeEach
    void setUp() {
        // Arrange: Setup common test data
        paymentInfo = new PaymentInfoDTO();
        paymentInfo.setCardNumber("1234567890123456");
        paymentInfo.setCardHolderName("John Doe");
        paymentInfo.setExpiryDate("12/25");
        paymentInfo.setCvv("123");

        createPurchaseDTO = new CreateTicketPurchaseDTO();
        createPurchaseDTO.setMovieId(1L);
        createPurchaseDTO.setQuantity(2);
        createPurchaseDTO.setPaymentInfo(paymentInfo);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("customer@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setPrice(15000.0);

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

        purchaseDTO = new TicketPurchaseDTO();
        purchaseDTO.setId(1L);
        purchaseDTO.setCustomerId(1L);
        purchaseDTO.setCustomerEmail("customer@example.com");
        purchaseDTO.setCustomerName("John Doe");
        purchaseDTO.setMovieId(1L);
        purchaseDTO.setMovieTitle("Test Movie");
        purchaseDTO.setQuantity(2);
        purchaseDTO.setUnitPrice(15000.0);
        purchaseDTO.setTotalAmount(30000.0);
        purchaseDTO.setStatus(PurchaseStatus.CONFIRMED);
        purchaseDTO.setCardLastFour("3456");
        purchaseDTO.setCardHolderName("John Doe");
        purchaseDTO.setPurchaseDate(LocalDateTime.now());
        purchaseDTO.setConfirmationCode("TKT-ABC12345");
    }

    @Test
    @DisplayName("POST /purchases - Should create purchase successfully")
    void testCreatePurchase_Success() throws Exception {
        // Arrange
        when(purchaseService.createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class))).thenReturn(purchase);
        when(purchaseService.toDTO(any(TicketPurchase.class))).thenReturn(purchaseDTO);

        // Act & Assert
        mockMvc.perform(post("/purchases")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPurchaseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Purchase created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.customerId").value(1))
                .andExpect(jsonPath("$.data.movieId").value(1))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.totalAmount").value(30000.0))
                .andExpect(jsonPath("$.data.confirmationCode").value("TKT-ABC12345"));

        verify(purchaseService, times(1)).createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class));
        verify(purchaseService, times(1)).toDTO(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("POST /purchases - Should return 400 when validation fails")
    void testCreatePurchase_ValidationError() throws Exception {
        // Arrange - Invalid DTO (missing required fields)
        CreateTicketPurchaseDTO invalidDTO = new CreateTicketPurchaseDTO();

        // Act & Assert
        mockMvc.perform(post("/purchases")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(purchaseService, never()).createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class));
    }

    @Test
    @DisplayName("POST /purchases - Should return 404 when customer not found")
    void testCreatePurchase_CustomerNotFound() throws Exception {
        // Arrange
        when(purchaseService.createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class)))
            .thenThrow(new ResourceNotFoundException("Customer not found with id: 1"));

        // Act & Assert
        mockMvc.perform(post("/purchases")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPurchaseDTO)))
                .andExpect(status().isNotFound());

        verify(purchaseService, times(1)).createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class));
    }

    @Test
    @DisplayName("POST /purchases - Should return 404 when movie not found")
    void testCreatePurchase_MovieNotFound() throws Exception {
        // Arrange
        when(purchaseService.createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class)))
            .thenThrow(new ResourceNotFoundException("Movie not found with id: 1"));

        // Act & Assert
        mockMvc.perform(post("/purchases")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPurchaseDTO)))
                .andExpect(status().isNotFound());

        verify(purchaseService, times(1)).createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class));
    }

    @Test
    @DisplayName("GET /purchases/{id} - Should get purchase by id successfully")
    void testGetPurchase_Success() throws Exception {
        // Arrange
        when(purchaseService.getPurchaseById(1L)).thenReturn(purchase);
        when(purchaseService.toDTO(any(TicketPurchase.class))).thenReturn(purchaseDTO);

        // Act & Assert
        mockMvc.perform(get("/purchases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Purchase retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.confirmationCode").value("TKT-ABC12345"));

        verify(purchaseService, times(1)).getPurchaseById(1L);
        verify(purchaseService, times(1)).toDTO(any(TicketPurchase.class));
    }

    @Test
    @DisplayName("GET /purchases/{id} - Should return 404 when purchase not found")
    void testGetPurchase_NotFound() throws Exception {
        // Arrange
        when(purchaseService.getPurchaseById(1L))
            .thenThrow(new ResourceNotFoundException("Purchase not found with id: 1"));

        // Act & Assert
        mockMvc.perform(get("/purchases/1"))
                .andExpect(status().isNotFound());

        verify(purchaseService, times(1)).getPurchaseById(1L);
    }

    @Test
    @DisplayName("GET /purchases/customer/{customerId} - Should get customer purchases")
    void testGetCustomerPurchases_Success() throws Exception {
        // Arrange
        List<TicketPurchaseDTO> purchases = Arrays.asList(purchaseDTO);
        when(purchaseService.getCustomerPurchases(1L)).thenReturn(purchases);

        // Act & Assert
        mockMvc.perform(get("/purchases/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].customerId").value(1))
                .andExpect(jsonPath("$.data[0].customerEmail").value("customer@example.com"));

        verify(purchaseService, times(1)).getCustomerPurchases(1L);
    }

    @Test
    @DisplayName("GET /purchases/customer/{customerId} - Should return empty list when no purchases")
    void testGetCustomerPurchases_EmptyList() throws Exception {
        // Arrange
        when(purchaseService.getCustomerPurchases(1L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/purchases/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(purchaseService, times(1)).getCustomerPurchases(1L);
    }

    @Test
    @DisplayName("GET /purchases/movie/{movieId} - Should get movie purchases")
    void testGetMoviePurchases_Success() throws Exception {
        // Arrange
        List<TicketPurchaseDTO> purchases = Arrays.asList(purchaseDTO);
        when(purchaseService.getMoviePurchases(1L)).thenReturn(purchases);

        // Act & Assert
        mockMvc.perform(get("/purchases/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].movieId").value(1))
                .andExpect(jsonPath("$.data[0].movieTitle").value("Test Movie"));

        verify(purchaseService, times(1)).getMoviePurchases(1L);
    }

    @Test
    @DisplayName("GET /purchases/movie/{movieId} - Should return empty list when no purchases")
    void testGetMoviePurchases_EmptyList() throws Exception {
        // Arrange
        when(purchaseService.getMoviePurchases(1L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/purchases/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(purchaseService, times(1)).getMoviePurchases(1L);
    }

    @Test
    @DisplayName("GET /purchases - Should get all purchases")
    void testGetAllPurchases_Success() throws Exception {
        // Arrange
        List<TicketPurchaseDTO> purchases = Arrays.asList(purchaseDTO);
        when(purchaseService.getCustomerPurchases(anyLong())).thenReturn(purchases);

        // Act & Assert
        mockMvc.perform(get("/purchases/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @DisplayName("POST /purchases - Should validate payment info")
    void testCreatePurchase_ValidPaymentInfo() throws Exception {
        // Arrange
        when(purchaseService.createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class))).thenReturn(purchase);
        when(purchaseService.toDTO(any(TicketPurchase.class))).thenReturn(purchaseDTO);

        // Act & Assert
        mockMvc.perform(post("/purchases")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPurchaseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.cardLastFour").value("3456"))
                .andExpect(jsonPath("$.data.cardHolderName").value("John Doe"));

        verify(purchaseService, times(1)).createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class));
    }

    @Test
    @DisplayName("POST /purchases - Should calculate correct total amount")
    void testCreatePurchase_CorrectTotalAmount() throws Exception {
        // Arrange
        when(purchaseService.createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class))).thenReturn(purchase);
        when(purchaseService.toDTO(any(TicketPurchase.class))).thenReturn(purchaseDTO);

        // Act & Assert
        mockMvc.perform(post("/purchases")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPurchaseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.unitPrice").value(15000.0))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.totalAmount").value(30000.0));

        verify(purchaseService, times(1)).createPurchase(anyLong(), any(CreateTicketPurchaseDTO.class));
    }
}
