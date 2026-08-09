package com.flmhospitals.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.flmhospitals.dao.AppointmentRepository;

@Component
public class AppointmentIdGenerator {
	
	private final AppointmentRepository appointmentRepository;
	
	public AppointmentIdGenerator(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}
	
	public String generateNextAppointmentId() {
		String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String lastId = appointmentRepository.findLastAppointment();
		
		int nextNumber = 1;
		
		if(lastId != null && lastId.length() > 14) {
			String lastDate = lastId.substring(0,14);
			
			if(prefix.equals(lastDate)) {
				String numberPart = lastId.substring(14);
				nextNumber= Integer.parseInt(numberPart) + 1;
				
				String suffix = String.format("%05d",nextNumber);
				
				return prefix+suffix;
			}
		}
		
		String suffix = String.format("%05d", nextNumber);
		
		return prefix + suffix;
	}


}
