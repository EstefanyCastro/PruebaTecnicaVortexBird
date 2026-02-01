package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.CustomerDTO;
import com.vortexbird.movieticket.dto.LoginDTO;
import com.vortexbird.movieticket.dto.LoginResponseDTO;
import com.vortexbird.movieticket.dto.RegisterCustomerDTO;
import com.vortexbird.movieticket.model.Customer;

import java.util.List;

/**
 * Service interface for Customer business logic.
 *
 * Defines operations for managing customers and authentication.
 */
public interface ICustomerService {

    Customer register(RegisterCustomerDTO dto);

    LoginResponseDTO login(LoginDTO dto);

    Customer getCustomerById(Long id);

    List<CustomerDTO> getAllCustomers();

    void disableCustomer(Long id);

    CustomerDTO toDTO(Customer customer);
}
