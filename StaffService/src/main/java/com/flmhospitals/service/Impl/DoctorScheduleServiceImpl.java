package com.flmhospitals.service.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flmhospitals.dao.DoctorScheduleRepository;
import com.flmhospitals.dao.StaffRepository;
import com.flmhospitals.enums.StaffType;
import com.flmhospitals.exception.StaffNotFoundException;
import com.flmhospitals.model.DoctorSchedule;
import com.flmhospitals.model.Staff;
import com.flmhospitals.service.DoctorScheduleService;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService{

	private final  StaffRepository staffRepository;
	private final DoctorScheduleRepository doctorScheduleRepository;
	
	public DoctorScheduleServiceImpl(StaffRepository staffRepository, DoctorScheduleRepository doctorScheduleRepository) {
		this.staffRepository = staffRepository;
		this.doctorScheduleRepository = doctorScheduleRepository;
	}
	
	@Override
	public ResponseEntity<String> markDoctorAvailable(String staffId, List<LocalDate> dates) {
		
		if(dates == null || dates.isEmpty()) {
			throw new IllegalArgumentException("Please provide atleast one date");
		}
		
		Staff staff = staffRepository.findById(staffId)
						.orElseThrow(() -> new StaffNotFoundException("No Staff with ID " +staffId) );
		
		if(staff.getStaffType() != StaffType.DOCTOR ) {
			throw new IllegalArgumentException("Staff with Id "+ staffId + "is not a doctor");
		}
		
		LocalDate today = LocalDate.now();
		List<LocalDate> invalidDates = new ArrayList<>();
		List<LocalDate> processedDates = new ArrayList<>();
		
		for(LocalDate date : dates) {
			if(date.isBefore(today)) {
				invalidDates.add(date);	
			}
		}
		
		if(!invalidDates.isEmpty()) {
			throw new IllegalArgumentException("Cannot mark availabilty for past dates : "+invalidDates);
		}
		
		for(LocalDate date : dates) {
			Optional<DoctorSchedule> existingschedule =
					doctorScheduleRepository.findByStaff_StaffIdAndUnavailableDate(staffId,date);
			
			if(existingschedule.isPresent()) {
				doctorScheduleRepository.delete(existingschedule.get());
				processedDates.add(date);
			}
		}
		
		if(processedDates.isEmpty()) {
			return ResponseEntity.ok("Doctor is already available on all selected dates");
		}
		
		return ResponseEntity.ok("Doctor marked availabile for "+ processedDates.size() + " date");
	}

	@Override
	public boolean isDoctorAvailable(String staffId, LocalDate date) {
		
		Staff staff = staffRepository.findById(staffId).orElseThrow(
				() ->  new StaffNotFoundException("Staff with ID :" + staffId + " not found") );
		
		if(staff.getStaffType() != StaffType.DOCTOR) {
			throw new IllegalArgumentException("Staff with ID "+ staffId + " not a doctor");
		}
		
		boolean isAvailable = !doctorScheduleRepository.existsByStaff_StaffIdAndUnavailableDate(staffId,date);
		
		return isAvailable;
	}

	@Override
	public List<LocalDate> markDoctorUnavailable(String doctorId, List<LocalDate> listOfUnavailableDates) {

		
		
		return null;
	}

}
