package com.vortexbird.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vortexbird.movieticket.dto.CustomerDTO;
import com.vortexbird.movieticket.dto.LoginDTO;
import com.vortexbird.movieticket.dto.LoginResponseDTO;
import com.vortexbird.movieticket.dto.RegisterCustomerDTO;
import com.vortexbird.movieticket.model.Customer;
import com.vortexbird.movieticket.model.Role;
import com.vortexbird.movieticket.service.ICustomerService;
import com.vortexbird.movieticket.shared.exception.BusinessException;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CustomerController.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mock service responses
 * - Act: Perform HTTP requests
 * - Assert: Verify response status and content
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security for tests
@ActiveProfiles("test") // Use test configuration with H2 database
@DisplayName("CustomerController Tests")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICustomerService customerService;

    private RegisterCustomerDTO validRegisterDTO;
    private LoginDTO validLoginDTO;
    private Customer customer;
    private CustomerDTO customerDTO;
    private LoginResponseDTO loginResponseDTO;

    @BeforeEach
    void setUp() {
        // Arrange: Setup common test data
        validRegisterDTO = new RegisterCustomerDTO();
        validRegisterDTO.setEmail("test@example.com");
        validRegisterDTO.setPhone("3001234567");
        validRegisterDTO.setFirstName("John");
        validRegisterDTO.setLastName("Doe");
        validRegisterDTO.setPassword("Password123");

        validLoginDTO = new LoginDTO();
        validLoginDTO.setEmail("test@example.com");
        validLoginDTO.setPassword("Password123");

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setPhone("3001234567");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPassword("Password123");
        customer.setRole(Role.CUSTOMER);
        customer.setIsEnabled(true);

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setEmail("test@example.com");
        customerDTO.setPhone("3001234567");
        customerDTO.setFirstName("John");
        customerDTO.setLastName("Doe");
        customerDTO.setRole("CUSTOMER");
        customerDTO.setEnabled(true);

        loginResponseDTO = new LoginResponseDTO(
            null,
            1L,
            "test@example.com",
            "John",
            "Doe",
            "CUSTOMER"
        );
    }

    @Test
    @DisplayName("POST /customers/register - Should register customer successfully")
    void testRegisterCustomer_Success() throws Exception {
        // Arrange
        when(customerService.register(any(RegisterCustomerDTO.class))).thenReturn(customer);
        when(customerService.toDTO(any(Customer.class))).thenReturn(customerDTO);

        // Act & Assert
        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer registered successfully"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));

        verify(customerService).register(any(RegisterCustomerDTO.class));
        verify(customerService).toDTO(any(Customer.class));
    }

    @Test
    @DisplayName("POST /customers/register - Should return 400 when email is invalid")
    void testRegisterCustomer_InvalidEmail() throws Exception {
        // Arrange
        validRegisterDTO.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).register(any(RegisterCustomerDTO.class));
    }

    @Test
    @DisplayName("POST /customers/register - Should return 400 when phone is invalid")
    void testRegisterCustomer_InvalidPhone() throws Exception {
        // Arrange
        validRegisterDTO.setPhone("123");

        // Act & Assert
        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).register(any(RegisterCustomerDTO.class));
    }

    @Test
    @DisplayName("POST /customers/register - Should return 400 when email already exists")
    void testRegisterCustomer_EmailAlreadyExists() throws Exception {
        // Arrange
        when(customerService.register(any(RegisterCustomerDTO.class)))
            .thenThrow(new BusinessException("Email already registered"));

        // Act & Assert
        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered"));

        verify(customerService).register(any(RegisterCustomerDTO.class));
    }

    @Test
    @DisplayName("POST /customers/login - Should login successfully")
    void testLogin_Success() throws Exception {
        // Arrange
        when(customerService.login(any(LoginDTO.class))).thenReturn(loginResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.customerId").value(1))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));

        verify(customerService).login(any(LoginDTO.class));
    }

    @Test
    @DisplayName("POST /customers/login - Should return 400 when credentials are invalid")
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        when(customerService.login(any(LoginDTO.class)))
            .thenThrow(new BusinessException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(customerService).login(any(LoginDTO.class));
    }

    @Test
    @DisplayName("POST /customers/login - Should return 400 when account is disabled")
    void testLogin_AccountDisabled() throws Exception {
        // Arrange
        when(customerService.login(any(LoginDTO.class)))
            .thenThrow(new BusinessException("Account is disabled"));

        // Act & Assert
        mockMvc.perform(post("/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Account is disabled"));

        verify(customerService).login(any(LoginDTO.class));
    }

    @Test
    @DisplayName("GET /customers/{id} - Should get customer by id successfully")
    void testGetCustomerById_Success() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(customerService.toDTO(any(Customer.class))).thenReturn(customerDTO);

        // Act & Assert
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));

        verify(customerService).getCustomerById(1L);
        verify(customerService).toDTO(any(Customer.class));
    }

    @Test
    @DisplayName("GET /customers/{id} - Should return 404 when customer not found")
    void testGetCustomerById_NotFound() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L))
            .thenThrow(new ResourceNotFoundException("Customer not found with id: 1"));

        // Act & Assert
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Customer not found with id: 1"));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("GET /customers - Should get all customers successfully")
    void testGetAllCustomers_Success() throws Exception {
        // Arrange
        CustomerDTO customer2DTO = new CustomerDTO();
        customer2DTO.setId(2L);
        customer2DTO.setEmail("admin@example.com");
        customer2DTO.setFirstName("Jane");
        customer2DTO.setLastName("Smith");
        customer2DTO.setRole("ADMIN");
        customer2DTO.setEnabled(true);

        List<CustomerDTO> customers = Arrays.asList(customerDTO, customer2DTO);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customers retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.data[1].email").value("admin@example.com"));

        verify(customerService).getAllCustomers();
    }

    @Test
    @DisplayName("DELETE /customers/{id} - Should disable customer successfully")
    void testDisableCustomer_Success() throws Exception {
        // Arrange
        doNothing().when(customerService).disableCustomer(1L);

        // Act & Assert
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer disabled successfully"));

        verify(customerService).disableCustomer(1L);
    }

    @Test
    @DisplayName("DELETE /customers/{id} - Should return 404 when customer not found")
    void testDisableCustomer_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Customer not found with id: 1"))
            .when(customerService).disableCustomer(1L);

        // Act & Assert
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Customer not found with id: 1"));

        verify(customerService).disableCustomer(1L);
    }

    @Test
    @DisplayName("POST /customers/register - Should return 400 when password is too short")
    void testRegisterCustomer_PasswordTooShort() throws Exception {
        // Arrange
        validRegisterDTO.setPassword("Pass1");

        // Act & Assert
        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).register(any(RegisterCustomerDTO.class));
    }

    @Test
    @DisplayName("POST /customers/register - Should return 400 when required fields are missing")
    void testRegisterCustomer_MissingFields() throws Exception {
        // Arrange
        RegisterCustomerDTO invalidDTO = new RegisterCustomerDTO();
        invalidDTO.setEmail("test@example.com");
        // Missing other required fields

        // Act & Assert
        mockMvc.perform(post("/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).register(any(RegisterCustomerDTO.class));
    }

    @Test
    @DisplayName("GET /customers - Should return empty list when no customers exist")
    void testGetAllCustomers_EmptyList() throws Exception {
        // Arrange
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(customerService).getAllCustomers();
    }
}
