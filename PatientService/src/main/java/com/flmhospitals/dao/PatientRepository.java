package com.flmhospitals.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flmhospitals.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String>{

	boolean existsByPatientEmail(String patientEmail);

	boolean existsByPatientPhoneNumber(String patientPhoneNumber);

	@Query(value = "SELECT patient_id FROM patients ORDER BY patient_id DESC LIMIT 1",nativeQuery = true)
	String findLastPatientId();

}
