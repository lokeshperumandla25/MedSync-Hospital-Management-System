package com.flmhospitals.model;


import java.time.LocalDateTime;

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
@Table(name = "staff_details")
@Builder
public class StaffDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long staffDetailsId;
	
	private String email;
	
	private String password;
	
	private String resetOtp;
	
	private LocalDateTime otpExpiryTime;
	
	private boolean requirePasswordReset = false;

	public StaffDetails(String email, String password, String resetOtp, LocalDateTime otpExpiryTime,
			boolean requirePasswordReset) {
		super();
		this.email = email;
		this.password = password;
		this.resetOtp = resetOtp;
		this.otpExpiryTime = otpExpiryTime;
		this.requirePasswordReset = requirePasswordReset;
	}
	
}
