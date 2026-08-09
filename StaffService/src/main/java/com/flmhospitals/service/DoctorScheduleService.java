package com.flmhospitals.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

public interface DoctorScheduleService {

	ResponseEntity<String> markDoctorAvailable(String staffId, List<LocalDate> dates);

	boolean isDoctorAvailable(String staffId, LocalDate localDate);

	List<LocalDate> markDoctorUnavailable(String doctorId, List<LocalDate> listOfUnavailableDates);

}
