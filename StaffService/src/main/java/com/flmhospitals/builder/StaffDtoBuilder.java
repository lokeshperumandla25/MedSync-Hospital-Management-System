package com.flmhospitals.builder;

import org.springframework.beans.BeanUtils;
import org.springframework.jmx.support.RegistrationPolicy;

import com.flmhospitals.dto.StaffAddressDto;
import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.model.Staff;
import com.flmhospitals.model.StaffAddress;

public class StaffDtoBuilder {

	public static StaffDetailsDto buildStaffDetailsDto(Staff registeredStaff) {

		return StaffDetailsDto.builder()
				.staffId(registeredStaff.getStaffId())
				.firstName(registeredStaff.getFirstName())
				.lastName(registeredStaff.getLastName())
				.phoneNumber(registeredStaff.getPhoneNumber())
				.role(registeredStaff.getRole())
				.gender(registeredStaff.getGender())
				.dateOfJoining(registeredStaff.getDateOfJoining())
				.experienceInYears(registeredStaff.getExperienceInYears())
				.email(registeredStaff.getEmail())
				.specialization(registeredStaff.getSpecialization())
				.staffType(registeredStaff.getStaffType())
				.isEmployeeActive(registeredStaff.isEmployeeActive())
				.canLogin(registeredStaff.isCanLogin())
				.staffAddressDto(buildAddressDto(registeredStaff.getStaffAddress()))
				.build();
	}

	private static StaffAddressDto buildAddressDto(StaffAddress staffAddress) {
		StaffAddressDto staffAddressDto = new StaffAddressDto();
		
		BeanUtils.copyProperties(staffAddress, staffAddressDto);
	
		return staffAddressDto;
	}
	
	

}
