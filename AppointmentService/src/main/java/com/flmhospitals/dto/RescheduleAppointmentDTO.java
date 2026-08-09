package com.flmhospitals.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RescheduleAppointmentDTO {
	
	private LocalDate newDate;
	
	private LocalTime newStartTime;
	
	private LocalTime newEndTime;
	

}
