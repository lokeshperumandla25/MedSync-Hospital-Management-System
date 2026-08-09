package com.flmhospitals.model;

import java.time.LocalDate;

import com.flmhospitals.utils.Gender;
import com.flmhospitals.utils.PatientEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "patients")
@Builder
@EntityListeners(value = PatientEntityListener.class)
public class Patient {
	
	@Id
	@Column(name = "patient_id",nullable = false,unique = true)
	private String patientId;
	
	private String patientName;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private String patientEmail;
	
	private String patientPhoneNumber;
	
	private LocalDate dateOfBirth;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "patient_address_id")
	private PatientAddress patientAddress;
	
	

}
