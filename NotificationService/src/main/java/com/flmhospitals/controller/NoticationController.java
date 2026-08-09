package com.flmhospitals.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flmhospitals.dto.EmailRequest;
import com.flmhospitals.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NoticationController {
	
	private final NotificationService notificationService;
	
	public NoticationController(NotificationService notificationService){
		this.notificationService = notificationService;	
	}
	
	@PostMapping("/email")
	public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest){
		notificationService.sendEmail(emailRequest);
		return ResponseEntity.ok("Email is sent successfully");	
	}

}
