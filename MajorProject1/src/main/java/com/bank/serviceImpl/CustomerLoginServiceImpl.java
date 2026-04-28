package com.bank.serviceImpl;

import org.springframework.stereotype.Service;

import com.bank.dao.CustomerRepository;
import com.bank.model.Customer;
import com.bank.service.CustomerLoginService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class CustomerLoginServiceImpl implements CustomerLoginService{
	
	private CustomerRepository customerRepository;
	
	public CustomerLoginServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	@Transactional
	@Override
	public String validateLogin(String accountNumber, String password) {
		
		if (accountNumber == null || accountNumber.isBlank())
            return "Account number required";

        if (password == null || password.isBlank())
            return "Password required";

        Customer customer = customerRepository.findAccountNumber(accountNumber);

        if (customer == null)
            return "Invalid account number";
        
        if(!customer.isActive())
        		return "Your account is deactivated. Please contact admin.";
        
        if(customer.isTempPasswordActive()) {
        		if(!password.equals(customer.getTempPassword()))
        			return "Invalid temporary password";
        		
        		return "RESET_REQUIRED";
        }
        
        if(!password.equals(customer.getPassword()))
        		return "Invalid password";
        
		
		return "SUCCESS";
	}
	
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	@Override
	public String resetPassword(String accountNumber, String newPassword) {
		
		if(newPassword == null || newPassword.length() <4)
			return "Password must be at least 4 characters";
		
		Customer customer = customerRepository.findAccountNumber(accountNumber);
		
		customer.setPassword(newPassword);
		customer.setTempPasswordActive(false);
		customer.setTempPassword(null);
		
		entityManager.merge(customer);
		return "SUCCESS";
	}

}
