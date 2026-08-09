package com.flmhospitals.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flmhospitals.model.Patient;

import jakarta.persistence.PrePersist;

@Component
public class PatientEntityListener {

	private static PatientIdGenerator patientIdGenerator;
	
	@Autowired
	public void init(PatientIdGenerator generator) {
		this.patientIdGenerator = generator;
	}
	
	@PrePersist
	public void OnPrePresist(Patient patient) {
		if(patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
			patient.setPatientId(patientIdGenerator.generateNextPatientId());
		}
	}
	
	
	
}
