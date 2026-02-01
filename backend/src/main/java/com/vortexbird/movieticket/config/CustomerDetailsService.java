package com.vortexbird.movieticket.config;

import com.vortexbird.movieticket.model.Customer;
import com.vortexbird.movieticket.repository.ICustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Custom UserDetailsService implementation.
 *
 * Loads user-specific data for Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService {

    private final ICustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .authorities(new ArrayList<>())
                .accountExpired(false)
                .accountLocked(!customer.getIsEnabled())
                .credentialsExpired(false)
                .disabled(!customer.getIsEnabled())
                .build();
    }
}
