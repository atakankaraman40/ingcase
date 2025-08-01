package com.ingcase.digitalwallet.service.impl;

import com.ingcase.digitalwallet.exception.CustomerNotFoundException;
import com.ingcase.digitalwallet.model.entity.Customer;
import com.ingcase.digitalwallet.repository.CustomerRepository;
import com.ingcase.digitalwallet.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }
}
