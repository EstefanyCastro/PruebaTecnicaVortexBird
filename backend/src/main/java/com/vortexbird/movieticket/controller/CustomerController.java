package com.vortexbird.movieticket.controller;

import com.vortexbird.movieticket.dto.CustomerDTO;
import com.vortexbird.movieticket.dto.LoginDTO;
import com.vortexbird.movieticket.dto.LoginResponseDTO;
import com.vortexbird.movieticket.dto.RegisterCustomerDTO;
import com.vortexbird.movieticket.model.Customer;
import com.vortexbird.movieticket.service.ICustomerService;
import com.vortexbird.movieticket.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Customer management.
 *
 * Handles HTTP requests for customer registration, authentication, and management.
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final ICustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerDTO>> register(@Valid @RequestBody RegisterCustomerDTO dto) {
        log.info("POST /customers/register - Registering customer: {}", dto.getEmail());
        Customer customer = customerService.register(dto);
        CustomerDTO customerDTO = customerService.toDTO(customer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(customerDTO, "Customer registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginDTO dto) {
        log.info("POST /customers/login - Login attempt for: {}", dto.getEmail());
        LoginResponseDTO response = customerService.login(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomer(@PathVariable Long id) {
        log.info("GET /customers/{} - Fetching customer", id);
        Customer customer = customerService.getCustomerById(id);
        CustomerDTO customerDTO = customerService.toDTO(customer);
        return ResponseEntity.ok(ApiResponse.success(customerDTO, "Customer retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers() {
        log.info("GET /customers - Fetching all customers (admin)");
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success(customers, "Customers retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> disableCustomer(@PathVariable Long id) {
        log.info("DELETE /customers/{} - Disabling customer (admin)", id);
        customerService.disableCustomer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Customer disabled successfully"));
    }
}
