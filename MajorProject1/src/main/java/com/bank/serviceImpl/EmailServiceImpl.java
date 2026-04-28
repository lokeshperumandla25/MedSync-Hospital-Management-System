package com.bank.serviceImpl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bank.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;
	
	public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

	
	@Override
	public void sendMail(String email, String subject, String body) {
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setTo(email);
		mailMessage.setSubject(subject);
		mailMessage.setText(body);
		
		mailSender.send(mailMessage);
	}

}
