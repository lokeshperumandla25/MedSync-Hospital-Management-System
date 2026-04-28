package com.bank.service;

public interface OtpService {

	void generateOtp(String accountNumber);

	boolean validateOtp(String accountNumber, String otp);

	

}
