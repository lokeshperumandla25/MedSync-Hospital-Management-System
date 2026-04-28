package com.bank.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.dao.CustomerRepository;
import com.bank.model.Customer;
import com.bank.service.AdminCustomerService;

@Service
public class AdminCustomerServiceImpl implements AdminCustomerService {
	
	private final CustomerRepository customerRepository;
	
	public AdminCustomerServiceImpl (CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public String toogleCustomerStatus(Long customerId) {
		
		Customer customer = customerRepository.findById(customerId);
		
		if(customer == null)
			return "Customer not found";
		
		customer.setActive(!customer.isActive());
		customerRepository.save(customer);
		
		return customer.isActive() ?
				"Customer activated successfully":
					"Customer deactivated successfully";
	}

}
