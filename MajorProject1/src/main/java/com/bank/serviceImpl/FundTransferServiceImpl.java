package com.bank.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.bank.dao.CustomerRepository;
import com.bank.dao.TransactionRepository;
import com.bank.model.Customer;
import com.bank.model.Transcation;
import com.bank.service.FundTransferService;

import jakarta.transaction.Transactional;

@Service
public class FundTransferServiceImpl implements FundTransferService {
	
	private final CustomerRepository customerRepository;
	private final TransactionRepository transactionRepository;
	
	public FundTransferServiceImpl(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
		this.customerRepository= customerRepository;
		this.transactionRepository = transactionRepository;
	}

	@Transactional
	@Override
	public String transfer(String fromAccount, String toAccount, double amount) {
		
		if(fromAccount == null || toAccount == null)
			return "Invalid account";
		
		if(fromAccount.equals(toAccount))
			return "Cannot transfer to same account";
		
		if(amount < 0) 
			return "Invalid transfer amount";
		
		Customer sender = customerRepository.findAccountNumber(fromAccount);
		
		Customer receiver = customerRepository.findAccountNumber(toAccount);
		
		if(sender == null)
			return "Sender account not found";
		
		if(receiver == null) {
			return "Receiver account not found";
		}
		
		if(!sender.isActive())
			return "Your account is deactivated. Transfer not allowed";
		
		if(!receiver.isActive())
			return "Receiver account is deactivated . Transfer not allowed";
		
		if(sender.getBalance() < amount)
			return "Insufficient balance";
		
		sender.setBalance(sender.getBalance() - amount);
		
		receiver.setBalance(receiver.getBalance() + amount);
		
		customerRepository.save(sender);
		customerRepository.save(receiver);
		
		Transcation transcation = new Transcation();
		transcation.setFromAccount(fromAccount);
		transcation.setToAccount(toAccount);
		transcation.setAmount(amount);
		transcation.setType("TRANSFER");
		transcation.setTransactionDate(LocalDateTime.now());
		
		transactionRepository.save(transcation);

		return "SUCCESS";
	}

	
	
}
