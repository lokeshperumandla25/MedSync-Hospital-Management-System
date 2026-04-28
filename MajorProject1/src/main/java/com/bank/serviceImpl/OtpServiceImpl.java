package com.bank.serviceImpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.bank.dao.CustomerRepository;
import com.bank.dao.OtpRepository;
import com.bank.model.Customer;
import com.bank.model.Otp;
import com.bank.service.EmailService;
import com.bank.service.OtpService;

import jakarta.transaction.Transactional;

@Service
public class OtpServiceImpl implements OtpService{
	
	private final OtpRepository otpRepository;
	private final CustomerRepository customerRepository;
	private final EmailService emailService;
	private final Random random = new Random();
	
	public OtpServiceImpl(OtpRepository otpRepository,CustomerRepository customerRepository,EmailService emailService) {
		this.customerRepository = customerRepository;
		this.otpRepository = otpRepository;
		this.emailService = emailService;
	}

	@Transactional
	@Override
	public void generateOtp(String accountNumber) {
		String otp = String.valueOf((int)(Math.random()*900000) +100000);
		
		Otp entity = new Otp();
		entity.setAccountNumber(accountNumber);
		entity.setOtp(otp);
		entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
		
		otpRepository.save(entity);
		
		Customer customer = customerRepository.findAccountNumber(accountNumber);
		
		if(customer == null) {
			return;
		}
		
		String subject = "Smart Bank OTP Verification";
		String body = 
				"Dear " + customer.getFullName() + ",\n\n"+
				"Your OTP is: "+otp +"\n"+
				"Valid for 5 minutes.\n\n"+
				"Regards,\nSmart Bank";
		
		emailService.sendMail(customer.getEmail(), subject, body);
		
	}

	
	@Override
	public boolean validateOtp(String accountNumber, String otp) {
		
		Otp otp2 = otpRepository.findValidOtp(accountNumber,otp);
//		System.out.println(otp2);
		
		if(otp2 == null) {
			return false;
		}
		
		if(otp2.getExpiryTime().isBefore(LocalDateTime.now()))
			return false;
		
		otpRepository.delete(otp2);
		
		return true;
	}

}
