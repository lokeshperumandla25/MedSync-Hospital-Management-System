package com.bank.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.dao.CustomerRepository;
import com.bank.dao.LoanRepository;
import com.bank.dao.TransactionRepository;
import com.bank.model.Customer;
import com.bank.model.Loan;
import com.bank.model.Transcation;
import com.bank.service.AdminLoanService;

@Service
public class AdminLoanServiceImpl implements AdminLoanService {
	
	private final CustomerRepository customerRepository;
	private final LoanRepository loanRepository;
	private final TransactionRepository transactionRepository;
	
	public AdminLoanServiceImpl(CustomerRepository customerRepository, LoanRepository loanRepository, TransactionRepository transactionRepository) {
		this.customerRepository = customerRepository;
		this.loanRepository = loanRepository;
		this.transactionRepository  = transactionRepository;
	}

	@Override
	public List<Loan> getPendingLoans() {
		return loanRepository.findByStatus("PENDING");
	}

	@Override
	public String approveLoan(Long loanId) {
		
		Loan loan = loanRepository.findByLoanId(loanId);
		System.out.println(loan);
		
		if(loan == null)
			return "Loan not found";
		
		if(!"PENDING".equals(loan.getStatus()))
			return "Loan already processed";
		
		Customer customer = customerRepository.findAccountNumber(loan.getAccountNumber());
		
		if(customer == null)
			return "Customer not found";
		
		if(!customer.isActive())
			return "Customer account is deactivated . Loan cannot be issued";
		
		customer.setBalance(customer.getBalance() + loan.getAmount());
		
		loan.setStatus("APPROVED");
		
		customerRepository.save(customer);
		loanRepository.save(loan);
		
		Transcation transcation = new Transcation();
		transcation.setFromAccount("BANK");
		transcation.setToAccount(customer.getAccountNumber());
		transcation.setAmount(loan.getAmount());
		transcation.setType("LOAN_CREDIT");
		transcation.setTransactionDate(LocalDateTime.now());
		
		transactionRepository.save(transcation);
		
		return "Loan approved successfully";
	}

	@Override
	public String rejectLoan(Long loanId) {
		
		Loan loan = loanRepository.findByLoanId(loanId);
		
		if(loan == null)
			return "Loan not found";
		
		loan.setStatus("REJECTED");
		loanRepository.save(loan);
		
		return "Loan rejected";
	}

}
