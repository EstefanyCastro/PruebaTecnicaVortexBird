package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.CustomerDTO;
import com.vortexbird.movieticket.dto.LoginDTO;
import com.vortexbird.movieticket.dto.LoginResponseDTO;
import com.vortexbird.movieticket.dto.RegisterCustomerDTO;
import com.vortexbird.movieticket.model.Customer;
import com.vortexbird.movieticket.model.Role;
import com.vortexbird.movieticket.repository.ICustomerRepository;
import com.vortexbird.movieticket.shared.exception.BusinessException;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mocks
 * - Act: Execute the method under test
 * - Assert: Verify the results
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private RegisterCustomerDTO validRegisterDTO;
    private LoginDTO validLoginDTO;
    private Customer customer;

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
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegister_Success() {
        // Arrange
        when(customerRepository.existsByEmail(validRegisterDTO.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        Customer result = customerService.register(validRegisterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getEmail(), result.getEmail());
        assertEquals(customer.getFirstName(), result.getFirstName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals(Role.CUSTOMER, result.getRole());
        verify(customerRepository).existsByEmail(validRegisterDTO.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when email already exists")
    void testRegister_EmailAlreadyExists() {
        // Arrange
        when(customerRepository.existsByEmail(validRegisterDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> customerService.register(validRegisterDTO)
        );
        assertEquals("Email already registered", exception.getMessage());
        verify(customerRepository).existsByEmail(validRegisterDTO.getEmail());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLogin_Success() {
        // Arrange
        when(customerRepository.findByEmail(validLoginDTO.getEmail())).thenReturn(Optional.of(customer));

        // Act
        LoginResponseDTO result = customerService.login(validLoginDTO);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getCustomerId());
        assertEquals(customer.getEmail(), result.getEmail());
        assertEquals(customer.getFirstName(), result.getFirstName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals("CUSTOMER", result.getRole());
        verify(customerRepository).findByEmail(validLoginDTO.getEmail());
    }

    @Test
    @DisplayName("Should throw BusinessException when email not found")
    void testLogin_EmailNotFound() {
        // Arrange
        when(customerRepository.findByEmail(validLoginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> customerService.login(validLoginDTO)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(customerRepository).findByEmail(validLoginDTO.getEmail());
    }

    @Test
    @DisplayName("Should throw BusinessException when password is incorrect")
    void testLogin_InvalidPassword() {
        // Arrange
        validLoginDTO.setPassword("WrongPassword");
        when(customerRepository.findByEmail(validLoginDTO.getEmail())).thenReturn(Optional.of(customer));

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> customerService.login(validLoginDTO)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(customerRepository).findByEmail(validLoginDTO.getEmail());
    }

    @Test
    @DisplayName("Should throw BusinessException when account is disabled")
    void testLogin_AccountDisabled() {
        // Arrange
        customer.setIsEnabled(false);
        when(customerRepository.findByEmail(validLoginDTO.getEmail())).thenReturn(Optional.of(customer));

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> customerService.login(validLoginDTO)
        );
        assertEquals("Account is disabled", exception.getMessage());
        verify(customerRepository).findByEmail(validLoginDTO.getEmail());
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void testGetCustomerById_Success() {
        // Arrange
        when(customerRepository.findByIdAndIsEnabledTrue(1L)).thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getEmail(), result.getEmail());
        verify(customerRepository).findByIdAndIsEnabledTrue(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer not found")
    void testGetCustomerById_NotFound() {
        // Arrange
        when(customerRepository.findByIdAndIsEnabledTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> customerService.getCustomerById(1L)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository).findByIdAndIsEnabledTrue(1L);
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void testGetAllCustomers_Success() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setEmail("test2@example.com");
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setRole(Role.ADMIN);
        customer2.setIsEnabled(true);

        List<Customer> customers = Arrays.asList(customer, customer2);
        when(customerRepository.findByIsEnabledTrue()).thenReturn(customers);

        // Act
        List<CustomerDTO> result = customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(customer.getEmail(), result.get(0).getEmail());
        assertEquals(customer2.getEmail(), result.get(1).getEmail());
        verify(customerRepository).findByIsEnabledTrue();
    }

    @Test
    @DisplayName("Should disable customer successfully")
    void testDisableCustomer_Success() {
        // Arrange
        when(customerRepository.findByIdAndIsEnabledTrue(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        customerService.disableCustomer(1L);

        // Assert
        assertFalse(customer.getIsEnabled());
        verify(customerRepository).findByIdAndIsEnabledTrue(1L);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Should convert Customer entity to DTO correctly")
    void testToDTO_Success() {
        // Act
        CustomerDTO result = customerService.toDTO(customer);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getEmail(), result.getEmail());
        assertEquals(customer.getPhone(), result.getPhone());
        assertEquals(customer.getFirstName(), result.getFirstName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals("CUSTOMER", result.getRole());
        assertTrue(result.getEnabled());
    }

    @Test
    @DisplayName("Should register customer with ADMIN role when specified")
    void testRegister_AdminRole() {
        // Arrange
        Customer adminCustomer = new Customer();
        adminCustomer.setId(2L);
        adminCustomer.setEmail("admin@example.com");
        adminCustomer.setRole(Role.ADMIN);
        adminCustomer.setIsEnabled(true);

        when(customerRepository.existsByEmail(validRegisterDTO.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(2L);
            saved.setRole(Role.ADMIN);
            return saved;
        });

        // Act
        Customer result = customerService.register(validRegisterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void testGetAllCustomers_EmptyList() {
        // Arrange
        when(customerRepository.findByIsEnabledTrue()).thenReturn(Arrays.asList());

        // Act
        List<CustomerDTO> result = customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).findByIsEnabledTrue();
    }
}
