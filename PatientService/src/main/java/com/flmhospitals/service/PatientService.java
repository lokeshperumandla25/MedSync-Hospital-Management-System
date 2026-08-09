package com.flmhospitals.service;

import java.util.List;

import com.flmhospitals.dto.RegisterPatientRequestDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;

public interface PatientService {

	RegisterPatientResponseDto registerPatient(RegisterPatientRequestDto registerPatientRequestDto);

	List<RegisterPatientResponseDto> getAllPatients();

	RegisterPatientResponseDto updatePatient(RegisterPatientRequestDto registerPatientRequestDto, String patientId);

	RegisterPatientResponseDto getPatientById(String patientId);

	String getPatientName(String patientId);

}
