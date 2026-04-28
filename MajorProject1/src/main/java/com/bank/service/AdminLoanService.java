package com.bank.service;

import java.util.List;

import com.bank.model.Loan;

public interface AdminLoanService {

	List<Loan> getPendingLoans();

	String approveLoan(Long loanId);

	String rejectLoan(Long loanId);

}
