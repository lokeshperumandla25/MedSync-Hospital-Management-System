package com.flmhospitals.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patient_address")
@Builder
public class PatientAddress {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long patientAddressId;
	
	private String doorNumber;
	
	private String landmark;
	
	private String city;
	
	private String state;
	
	private String pinCode;
	
	private String country;

	public PatientAddress(String doorNumber, String landmark, String city, String state, String pincode,
			String country) {
		super();
		this.doorNumber = doorNumber;
		this.landmark = landmark;
		this.city = city;
		this.state = state;
		this.pinCode = pincode;
		this.country = country;
	}

}
