package com.bank.service;

import java.util.List;

import com.bank.model.Customer;

public interface AdminCustomerService {

	List<Customer> getAllCustomers();

	String toogleCustomerStatus(Long customerId);

}
