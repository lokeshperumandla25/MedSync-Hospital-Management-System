package com.flmhospitals.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.flmhospitals.dto.EmailRequestDto;

@FeignClient(name = "NotificationService")
public interface NotificationClient {
	
	@PostMapping("/api/notifications/email")
	public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDto emailRequest);

}
