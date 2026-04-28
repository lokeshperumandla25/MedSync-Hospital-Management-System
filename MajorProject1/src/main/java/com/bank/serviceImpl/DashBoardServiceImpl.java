package com.bank.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.stereotype.Service;

import com.bank.dao.CustomerRepository;
import com.bank.dao.TransactionRepository;
import com.bank.model.Customer;
import com.bank.model.Transcation;
import com.bank.service.DashBoardService;

import jakarta.transaction.Transaction;


@Service
public class DashBoardServiceImpl implements DashBoardService{
	
	private final CustomerRepository customerRepository;
	private final TransactionRepository transactionRepository;
	
	public DashBoardServiceImpl(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
		this.customerRepository = customerRepository;
		this.transactionRepository = transactionRepository;
	}

	@Override
	public Customer getCustomerByAccount(String accountNumber) {
		return customerRepository.findAccountNumber(accountNumber) ;
	}

	@Override
	public List<Transcation> getMiniStatement(String accNo) {
		List<Transcation> list = transactionRepository.findRecentTransactions(accNo);
		
		return list.size() > 10 ? list.subList(0, 10) : list;
	}

	@Override
	public List<Transaction> getFilteredStatement(String accNo, Integer month, Integer year) {
		// TODO Auto-generated method stub
		return null;
	}
	        

	
	
}
