package com.flmhospitals.builder;

import java.util.Random;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.flmhospitals.dto.RegisterStaffDto;
import com.flmhospitals.dto.StaffAddressDto;
import com.flmhospitals.model.Staff;
import com.flmhospitals.model.StaffAddress;
import com.flmhospitals.model.StaffDetails;

public class StaffBuilder {
	
	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	private static final ThreadLocal<String> plainPasswordHolder = new ThreadLocal<>();

	public static Staff buildStaffFromRegisteredStaffDto(RegisterStaffDto registerStaffDto) {
		
		return Staff.builder()
			.firstName(registerStaffDto.getFirstName())
			.lastName(registerStaffDto.getLastName())
			.phoneNumber(registerStaffDto.getPhoneNumber())
			.gender(registerStaffDto.getGender())
			.dateOfJoining(registerStaffDto.getDateOfJoining())
			.staffType(registerStaffDto.getStaffType())
			.specialization(registerStaffDto.getSpecialization())
			.experienceInYears(registerStaffDto.getExperienceInYears())
			.email(registerStaffDto.getEmail())
			.role(registerStaffDto.getRole())
			.canLogin(true)
			.isEmployeeActive(true)
			.staffAddress(buildStaffAddressFromStaffAddressDto(registerStaffDto.getStaffAddrssDto()))
			.staffDetails(buildStaffDetailsFromStaffDetailsDto(registerStaffDto.getEmail()))
			.build();
		
		
	}

	private static StaffDetails buildStaffDetailsFromStaffDetailsDto(String email) {

		String plainPassword = generateSixDigitPassword(); 
		String hashedPassword = passwordEncoder.encode(plainPassword);
		
		plainPasswordHolder.set(plainPassword);
		
		return StaffDetails.builder()
				.email(email)
				.password(hashedPassword)
				.requirePasswordReset(true)
				.build();
	}

	private static StaffAddress buildStaffAddressFromStaffAddressDto(StaffAddressDto staffAddrssDto) {
		
		StaffAddress staffAddress = new StaffAddress();
		BeanUtils.copyProperties(staffAddrssDto, staffAddress);
		
		return staffAddress;
	}
	
	private static String generateSixDigitPassword() {
		Random random = new Random();
		int password = 100000 + random.nextInt(900000);
		return String.valueOf(password);
	}
	
	public static String getPlainPassword() {
		return plainPasswordHolder.get();
	}
	
	public static void clearPlainPassword() {
		plainPasswordHolder.remove();
	}

	public static Staff updateStaffBuilder(RegisterStaffDto dto, Staff existingStaff) {
		existingStaff.setFirstName(dto.getFirstName());
	    existingStaff.setLastName(dto.getLastName());
	    existingStaff.setPhoneNumber(String.valueOf(dto.getPhoneNumber()));
	    existingStaff.setGender(dto.getGender());
	    existingStaff.setDateOfJoining(dto.getDateOfJoining());
	    existingStaff.setStaffType(dto.getStaffType());
	    existingStaff.setSpecialization(dto.getSpecialization());
	    existingStaff.setExperienceInYears(dto.getExperienceInYears());
	    existingStaff.setRole(dto.getRole() != null ? dto.getRole().toUpperCase() : null);
	    existingStaff.setEmail(dto.getEmail());
	    
	    StaffDetails details = existingStaff.getStaffDetails();
	    details.setEmail(dto.getEmail());
	    
	    StaffAddress address = existingStaff.getStaffAddress();
	    StaffAddressDto addressDto = dto.getStaffAddrssDto();
	    
	    address.setLandmark(addressDto.getLandmark());
	    address.setCity(addressDto.getCity());
	    address.setState(addressDto.getState());
	    address.setCountry(addressDto.getCountry());
	    address.setPincode(addressDto.getPincode());
	    
	    existingStaff.setStaffAddress(address);
	    existingStaff.setStaffDetails(details);
	    
		return existingStaff;
	}
	
	

	
	
	
	
	

}
