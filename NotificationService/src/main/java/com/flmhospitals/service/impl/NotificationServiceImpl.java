package com.flmhospitals.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;


import com.flmhospitals.config.SendGridProperties;
import com.flmhospitals.dto.EmailRequest;
import com.flmhospitals.service.NotificationService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class NotificationServiceImpl implements NotificationService{
	
	private final SendGridProperties properties;
	
	public NotificationServiceImpl (SendGridProperties properties) {
		this.properties = properties;
	}

	@Override
	public void sendEmail(EmailRequest emailRequest) {
		
		Email from = new Email(properties.getFromEmail(),
								properties.getFromName());
		
		Email to = new Email(emailRequest.getTo());
		
		Content content = new Content("text/html",emailRequest.getBody());
		
		Mail mail = new Mail(from,emailRequest.getSubject(),to,content);
		
		SendGrid sendGrid = new SendGrid(properties.getApiKey());
		
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			
			Response response = sendGrid.api(request);
			
			 if (response.getStatusCode() != 202) {
	                String errorMsg = "Failed to send email. Status Code: " + response.getStatusCode() + ", Body: " + response.getBody();
	                System.err.println(errorMsg);
	                throw new RuntimeException(errorMsg);
	            }
	            
	            System.out.println("=== Email sent successfully via SendGrid to: " + emailRequest.getTo() + " ===");
		}catch (IOException e) {
            System.err.println("=== SendGrid IOException ===");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("SendGrid Error: " + e.getMessage(), e);
		}
		catch(Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("unexpected error sending email: "+exception.getMessage());
		}
		
		
		
	}

}
