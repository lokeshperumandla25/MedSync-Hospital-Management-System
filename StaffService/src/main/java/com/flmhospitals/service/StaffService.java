package com.flmhospitals.service;


import java.util.List;

import org.springframework.http.ResponseEntity;

import com.flmhospitals.dto.LoginRequest;
import com.flmhospitals.dto.LoginResponse;
import com.flmhospitals.dto.RegisterStaffDto;
import com.flmhospitals.dto.ResetPasswordRequest;
import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.dto.VerifyOtpRequest;
import com.flmhospitals.model.Staff;

public interface StaffService {

	StaffDetailsDto registerStaffDetails(RegisterStaffDto registerStaffDto);

	LoginResponse login(LoginRequest request);

	List<StaffDetailsDto> getAllStaff();

	Staff getStaffByStaffId(String staffId);

	ResponseEntity<List<StaffDetailsDto>> searchByStaffFirstNameorLastName(String name);

	StaffDetailsDto updateStaff(String staffId, RegisterStaffDto registerStaffDto);

	void sendOtp(String email);

	void verifyOtp(VerifyOtpRequest otpRequest);

	void resetPassword(ResetPasswordRequest request);

	String deleteStaff(String staffId);

	String getDoctorName(String doctorId);
	
	

}
