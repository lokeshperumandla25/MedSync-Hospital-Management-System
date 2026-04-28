package com.bank.service;

import java.util.List;

import com.bank.model.Customer;
import com.bank.model.Transcation;

import jakarta.transaction.Transaction;

public interface DashBoardService {

	Customer getCustomerByAccount(String accountNumber);

	List<Transcation> getMiniStatement(String accNo);

	List<Transaction> getFilteredStatement(String accNo, Integer month, Integer year);

}
