package com.bank.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long loanId;
	
	private String accountNumber;
	
	private String loanType;
	
	private double amount;
	
	private int tenureMonths;
	
	private double interestRate;
	
	private String status;
	
	private LocalDate appliedDate;
    
    private LocalDateTime decisionDate;

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getTenureMonths() {
		return tenureMonths;
	}

	public void setTenureMonths(int tenureMonths) {
		this.tenureMonths = tenureMonths;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(LocalDate appliedDate) {
		this.appliedDate = appliedDate;
	}

	public LocalDateTime getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(LocalDateTime decisionDate) {
		this.decisionDate = decisionDate;
	}

	@Override
	public String toString() {
		return "Loan [loanId=" + loanId + ", accountNumber=" + accountNumber + ", loanType=" + loanType + ", amount="
				+ amount + ", tenureMonths=" + tenureMonths + ", interestRate=" + interestRate + ", status=" + status
				+ ", appliedDate=" + appliedDate + ", decisionDate=" + decisionDate + "]";
	}
	
	
	
}
