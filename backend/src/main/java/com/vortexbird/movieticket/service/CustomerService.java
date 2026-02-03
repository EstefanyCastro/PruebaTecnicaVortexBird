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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of Customer Service.
 * Uses BCrypt for password hashing and verification.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService implements ICustomerService {

    private final ICustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Customer register(RegisterCustomerDTO dto) {
        log.info("Registering new customer: {}", dto.getEmail());
        
        if (customerRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", dto.getEmail());
            throw new BusinessException("Email already registered");
        }
        
        Customer customer = new Customer();
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setRole(Role.CUSTOMER);
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer registered successfully: {}", savedCustomer.getEmail());
        return savedCustomer;
    }

    @Override
    public LoginResponseDTO login(LoginDTO dto) {
        log.info("Login attempt for email: {}", dto.getEmail());
        
        Customer customer = customerRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> {
                log.warn("Login failed: Email not found: {}", dto.getEmail());
                return new BusinessException("Invalid credentials");
            });
        
        if (!customer.getIsEnabled()) {
            log.warn("Login failed: Customer account disabled: {}", dto.getEmail());
            throw new BusinessException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
            log.warn("Login failed: Invalid password for email: {}", dto.getEmail());
            throw new BusinessException("Invalid credentials");
        }
        
        log.info("Login successful for customer: {}", customer.getEmail());
        
        return new LoginResponseDTO(
            customer.getId(),
            customer.getEmail(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getRole().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        log.info("Fetching customer with id: {}", id);
        return customerRepository.findByIdAndIsEnabledTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        log.info("Fetching all enabled customers");
        return customerRepository.findByIsEnabledTrue()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void disableCustomer(Long id) {
        log.info("Disabling customer with id: {}", id);
        Customer customer = getCustomerById(id);
        customer.setIsEnabled(false);
        customerRepository.save(customer);
        log.info("Customer disabled successfully: {}", customer.getEmail());
    }

    @Override
    public CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setRole(customer.getRole().name());
        dto.setEnabled(customer.getIsEnabled());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
}
