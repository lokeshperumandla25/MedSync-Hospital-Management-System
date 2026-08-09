package com.flmhospitals.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.flmhospitals.dto.RegisterPatientResponseDto;

@FeignClient(name = "PatientService")
public interface PatientClient {
	
	@GetMapping("/patients/{id}")
	public ResponseEntity<RegisterPatientResponseDto> getPatientById(@PathVariable(name = "id") String patientId);


	@GetMapping("/patients/getPatientName/{patientId}")
	String getPatientName(@PathVariable(name="patientId") String patientId);
}
