package com.flmhospitals.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "doctor_schedule")
public class DoctorSchedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long doctorScheduleId;
	
	@ManyToOne
	@JoinColumn(name = "staff_id", nullable = false)
	private Staff staff;
	
	@Column(name = "unavailable_date", nullable = false)
	private LocalDate unavailableDate;
	
	

}
