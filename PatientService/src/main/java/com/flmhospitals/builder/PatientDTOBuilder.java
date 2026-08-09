package com.flmhospitals.builder;

import java.util.List;

import com.flmhospitals.dto.PatientAddressResponseDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;
import com.flmhospitals.model.Patient;
import com.flmhospitals.model.PatientAddress;

public class PatientDTOBuilder {

	public static RegisterPatientResponseDto buildPatientDtoFromPatient(Patient patient) {
		
		return RegisterPatientResponseDto.builder()
				.patientId(patient.getPatientId())
				.patientName(patient.getPatientName())
				.gender(patient.getGender())
				.patientEmail(patient.getPatientEmail())
				.patientPhoneNumber(patient.getPatientPhoneNumber())
				.dateOfBirth(patient.getDateOfBirth())
				.patientAddress(buildFromPatientAddressToPatientResponseDto(patient.getPatientAddress()))
				.build();
	}

	private static PatientAddressResponseDto buildFromPatientAddressToPatientResponseDto(
			PatientAddress patientAddress) {
		
		return PatientAddressResponseDto.builder()
				.patientAddressId(patientAddress.getPatientAddressId())
				.doorNumber(patientAddress.getDoorNumber())
				.landmark(patientAddress.getLandmark())
				.city(patientAddress.getCity())
				.state(patientAddress.getState())
				.country(patientAddress.getCountry())
				.pinCode(patientAddress.getPinCode())
				.build();
	}
	
	public static List<RegisterPatientResponseDto> fromListPatientToListOfRegisterPatientResponseDto(List<Patient> patients){
		
		return patients
			.stream()
			.map(patient -> buildPatientDtoFromPatient(patient))
			.toList();
	}

}
