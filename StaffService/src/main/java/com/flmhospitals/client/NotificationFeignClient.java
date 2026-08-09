package com.flmhospitals.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.flmhospitals.dto.EmailRequestDto;

@FeignClient(name = "NotificationService" )
public interface NotificationFeignClient {

	@PostMapping("/api/notifications/email")
	public String sendEmail(@RequestBody EmailRequestDto emailRequest);
}
