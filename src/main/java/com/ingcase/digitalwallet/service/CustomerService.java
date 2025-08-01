package com.ingcase.digitalwallet.service;

import com.ingcase.digitalwallet.model.entity.Customer;

public interface CustomerService {

    Customer findById(Long id);
}
