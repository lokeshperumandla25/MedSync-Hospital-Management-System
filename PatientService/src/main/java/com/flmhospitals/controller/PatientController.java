package com.flmhospitals.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.flmhospitals.dto.RegisterPatientRequestDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;
import com.flmhospitals.service.PatientService;


@RestController
@RequestMapping("/patients")
public class PatientController {
	
	private final PatientService patientService;
	
	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<RegisterPatientResponseDto> registerPatient(
			@RequestBody RegisterPatientRequestDto registerPatientRequestDto){
		
		RegisterPatientResponseDto registerPatientResponseDto = patientService.registerPatient(registerPatientRequestDto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(registerPatientResponseDto);
	}
	
	@GetMapping()
	public ResponseEntity<List<RegisterPatientResponseDto>> getAllPatients(){
		List<RegisterPatientResponseDto> allPatients = patientService.getAllPatients();
		
		return ResponseEntity.status(HttpStatus.OK).body(allPatients);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<RegisterPatientResponseDto> updatePatient(@RequestBody RegisterPatientRequestDto registerPatientRequestDto,@PathVariable(name = "id") String patientId){
		RegisterPatientResponseDto registerPatientResponseDto = patientService.updatePatient(registerPatientRequestDto,patientId);
		
		return ResponseEntity.status(HttpStatus.OK).body(registerPatientResponseDto);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<RegisterPatientResponseDto> getPatientById(@PathVariable(name = "id") String patientId){
		RegisterPatientResponseDto patientResponseDto = patientService.getPatientById(patientId);
		
		return ResponseEntity.status(HttpStatus.OK).body(patientResponseDto);
	}
	
	@GetMapping("/getPatientName/{patientId}")
	public String getPatientName(@PathVariable(name = "patientId") String patientId) {
		
		return patientService.getPatientName(patientId);
	}
	
	

}
