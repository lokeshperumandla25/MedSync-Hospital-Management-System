package com.flmhospitals.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flmhospitals.model.Appointment;

import jakarta.persistence.PrePersist;

@Component
public class AppointmentEntityListner {
	
	public static AppointmentIdGenerator appointmentIdGenerator;
	
	@Autowired
	public void init(AppointmentIdGenerator appointmentIdGenerator) {
		this.appointmentIdGenerator = appointmentIdGenerator;
	}
	
	@PrePersist
	public void generateAppointmentId(Appointment appointment) {
		
		if(appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
			String appointmentId = appointmentIdGenerator.generateNextAppointmentId();
			appointment.setAppointmentId(appointmentId);
		}
		
		if(appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
			appointment.setStatus("Booked");
		}
	}
	
	

}
