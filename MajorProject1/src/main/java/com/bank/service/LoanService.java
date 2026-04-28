package com.bank.service;

import java.util.List;

import com.bank.model.Loan;

public interface LoanService {

	String applyLoan(String accNo, String loanType, double amount, int tenure);

	List<Loan> getLoans(String accNo);

}
