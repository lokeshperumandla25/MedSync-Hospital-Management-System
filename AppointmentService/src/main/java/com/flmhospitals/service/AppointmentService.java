package com.flmhospitals.service;

import java.time.LocalDate;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.dto.DiagnosisRequestDTO;
import com.flmhospitals.dto.RescheduleAppointmentDTO;

public interface AppointmentService {

	AppointmentResponseDTO bookAppointment(AppointmentRequestDTO appointmentRequestDTO);

	List<AppointmentResponseDTO> getAllAppointmentsForAllDoctors(LocalDate date);
	
	List<AppointmentResponseDTO> getAllAppointmentsOfDoctor(String doctorId, LocalDate date);

	List<String> getPatientByDoctor(String staffId, LocalDate startDate, LocalDate endDate);

	boolean cancelAppointment(String appointmentId);

	List<AppointmentResponseDTO> getAllFutureAppointmentsOfDoctor(String doctorId);

	AppointmentResponseDTO rescheduleAppointment(String appointmentId,
			RescheduleAppointmentDTO rescheduleAppointmentDTO);

	AppointmentResponseDTO getAppointmentDetails(String appointmentId);

	AppointmentResponseDTO submitDiagnosis(String appointmentId, DiagnosisRequestDTO diagnosisRequestDTO);

}
