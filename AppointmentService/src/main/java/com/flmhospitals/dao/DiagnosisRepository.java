package com.flmhospitals.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flmhospitals.model.Diagnosis;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

	Optional<Diagnosis> findByAppointmentId(String appointmentId);


}
