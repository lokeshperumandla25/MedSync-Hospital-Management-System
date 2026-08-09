package com.flmhospitals.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "StaffService")
public interface DoctorClient {

	@GetMapping("/staff/getDoctorName/{doctorId}")
	String getDoctorName(@PathVariable(name="doctorId") String doctorId);
}
