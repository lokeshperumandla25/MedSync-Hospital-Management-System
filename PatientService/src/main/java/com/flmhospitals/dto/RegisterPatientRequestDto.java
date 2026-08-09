package com.flmhospitals.dto;

import java.time.LocalDate;

import com.flmhospitals.utils.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPatientRequestDto {
	
	private String patientName;
	
	private String patientPhoneNumber;
	
	private Gender gender;
	
	private String patientEmail;
	
	private LocalDate dateOfBirth;
	
	private PatientAddressrequestDto patientAddress;
	

}
