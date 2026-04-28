package com.bank.service;

public interface FundTransferService {

	String transfer(String fromAccount, String toAccount, double amount);

}
