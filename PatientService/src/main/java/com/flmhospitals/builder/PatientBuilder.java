package com.flmhospitals.builder;

import org.springframework.beans.BeanUtils;

import com.flmhospitals.dto.PatientAddressrequestDto;
import com.flmhospitals.dto.RegisterPatientRequestDto;
import com.flmhospitals.model.Patient;
import com.flmhospitals.model.PatientAddress;

public class PatientBuilder {

	public static Patient buildPatientFromRegisterPatientRequestDto(RegisterPatientRequestDto registerPatientRequestDto) {
		
		return  Patient.builder()
				.patientName(registerPatientRequestDto.getPatientName())
				.gender(registerPatientRequestDto.getGender())
				.patientEmail(registerPatientRequestDto.getPatientEmail())
				.patientPhoneNumber(registerPatientRequestDto.getPatientPhoneNumber())
				.dateOfBirth(registerPatientRequestDto.getDateOfBirth())
				.patientAddress(buildPatientAddressFromPatientAddressRequestDto(registerPatientRequestDto.getPatientAddress()))
				.build();
	}

	private static PatientAddress buildPatientAddressFromPatientAddressRequestDto(
			PatientAddressrequestDto patientAddressRequestDto) {
		
		PatientAddress patientAddress = new PatientAddress();
		BeanUtils.copyProperties(patientAddressRequestDto, patientAddress);
		
		return patientAddress;
	}

}
