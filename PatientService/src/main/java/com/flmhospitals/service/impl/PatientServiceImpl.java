package com.flmhospitals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flmhospitals.builder.PatientBuilder;
import com.flmhospitals.builder.PatientDTOBuilder;
import com.flmhospitals.dao.PatientRepository;
import com.flmhospitals.dto.RegisterPatientRequestDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;
import com.flmhospitals.exception.PatientNotFoundException;
import com.flmhospitals.model.Patient;
import com.flmhospitals.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;
	
	public PatientServiceImpl(PatientRepository patientRepository ) {
		this.patientRepository = patientRepository;
	}
	
	@Override
	public RegisterPatientResponseDto registerPatient(RegisterPatientRequestDto registerPatientRequestDto) {

		if(patientRepository.existsByPatientEmail(registerPatientRequestDto.getPatientEmail())) {
			throw new IllegalArgumentException("Email " + registerPatientRequestDto.getPatientEmail() + " is already exists");
		}
		
		if(patientRepository.existsByPatientPhoneNumber(registerPatientRequestDto.getPatientPhoneNumber())) {
			throw new IllegalArgumentException("Phone number " + registerPatientRequestDto.getPatientPhoneNumber() + " is already exists");
		}
		
		Patient patient = PatientBuilder.buildPatientFromRegisterPatientRequestDto(registerPatientRequestDto);
		
		Patient savedPatient = patientRepository.save(patient);
		
		return PatientDTOBuilder.buildPatientDtoFromPatient(savedPatient);
	}

	@Override
	public List<RegisterPatientResponseDto> getAllPatients() {
		
		List<Patient> allPatients = patientRepository.findAll();
		
		return PatientDTOBuilder.fromListPatientToListOfRegisterPatientResponseDto(allPatients);
	}

	@Override
	public RegisterPatientResponseDto updatePatient(RegisterPatientRequestDto registerPatientRequestDto,
			String patientId) {

		Patient existingPatient = patientRepository.findById(patientId)
				.orElseThrow(() -> new PatientNotFoundException("No patient found with Id " + patientId));
		
		Patient updatedPatient = PatientBuilder.buildPatientFromRegisterPatientRequestDto(registerPatientRequestDto);
		
		updatedPatient.setPatientId(patientId);
		
		if(existingPatient.getPatientAddress() != null && updatedPatient.getPatientAddress() != null) {
			updatedPatient.getPatientAddress().setPatientAddressId(existingPatient.getPatientAddress().getPatientAddressId());
		}
		
		Patient savedPatient = patientRepository.save(updatedPatient);
		
		return PatientDTOBuilder.buildPatientDtoFromPatient(savedPatient);
	}

	@Override
	public RegisterPatientResponseDto getPatientById(String patientId) {
		Patient patient = patientRepository.findById(patientId)
					.orElseThrow(() -> new PatientNotFoundException("No Patient found with Id "+patientId));
		
		return PatientDTOBuilder.buildPatientDtoFromPatient(patient);
	}

	@Override
	public String getPatientName(String patientId) {

		Patient patient = patientRepository.findById(patientId)
							.orElseThrow(() -> new PatientNotFoundException("Patient with Id : "+patientId + " not found"));
		
		
		return patient.getPatientName();
	}
	
	
	
	

	
	
}
