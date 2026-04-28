package com.bank.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.dao.LoanRepository;
import com.bank.model.Loan;
import com.bank.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {
	
	private final LoanRepository loanRepository;
	
	public LoanServiceImpl (LoanRepository loanRepository) {
		this.loanRepository = loanRepository;
	}

	@Override
	public String applyLoan(String accNo, String loanType, double amount, int tenure) {
		
		if(amount <= 0)
			return "Invalid loan amount";
		
		if(tenure <= 0)
			return "Invalid tenure";
		
		Loan loan = new Loan();
		loan.setAccountNumber(accNo);
		loan.setAmount(amount);
		loan.setLoanType(loanType);
		loan.setTenureMonths(tenure);
		loan.setInterestRate(getInterestRate(loanType));
		loan.setStatus("PENDING");
		loan.setAppliedDate(LocalDate.now());
		
		loanRepository.save(loan);
		
		return "Loan applied successfully";
	}

	private double getInterestRate(String loanType) {
		return switch(loanType) {
			case "HOME" -> 8.5;
			case "EDUCATION" -> 6.5;
			default -> 12.0;
		};
	}

	@Override
	public List<Loan> getLoans(String accNo) {
		return loanRepository.findByAccountNumber(accNo);
	}

}
