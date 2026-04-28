package com.bank.service;

public interface CustomerLoginService {

	String validateLogin(String accountNumber, String password);

	String resetPassword(String accountNumber, String newPassword);

}
