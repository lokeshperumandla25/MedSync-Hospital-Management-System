package com.flmhospitals.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flmhospitals.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

	@Query(value = "SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1", nativeQuery = true)
	String findLastAppointment();

	@Query("SELECT a FROM Appointment a where a.doctorId = :doctorId AND a.appointmentDate = :appointmentDate and a.startTime < :endTime AND a.endTime > :startTime AND a.status != 'CANCELLED'")
	List<Appointment> findAppointmentsByDoctorId(@Param("doctorId") String doctorId,@Param("appointmentDate") LocalDate appointmentDate,@Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime);

	@Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.appointmentDate = :appointmentDate AND a.startTime < :endTime AND a.endTime > :startTime AND a.status != 'CANCELLED'")
	List<Appointment> findByPatientId(@Param("patientId") String patientId,@Param("appointmentDate") LocalDate appointmentDate,@Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime);

	List<Appointment> findByAppointmentDate(LocalDate date);

	@Query("SELECT DISTINCT patientId FROM Appointment WHERE doctorId = :staffId AND appointmentDate BETWEEN LEAST(:startDate, :endDate) AND GREATEST(:startDate, :endDate)")
	List<String> findByPatientsByStaffId(@Param("staffId") String staffId,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);

	List<Appointment> findByDoctorId(String doctorId);


}
