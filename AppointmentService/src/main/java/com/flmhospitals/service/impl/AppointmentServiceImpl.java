package com.flmhospitals.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.flmhospitals.builder.AppointmentBuilder;
import com.flmhospitals.builder.AppointmentDTOBuilder;
import com.flmhospitals.clients.DoctorClient;
import com.flmhospitals.clients.NotificationClient;
import com.flmhospitals.clients.PatientClient;
import com.flmhospitals.dao.AppointmentRepository;
import com.flmhospitals.dao.DiagnosisRepository;
import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.dto.DiagnosisRequestDTO;
import com.flmhospitals.dto.EmailRequestDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;
import com.flmhospitals.dto.RescheduleAppointmentDTO;
import com.flmhospitals.exception.AppointmentAlreadyExistsException;
import com.flmhospitals.exception.AppointmentNotFoundException;
import com.flmhospitals.exception.InvalidTimeException;
import com.flmhospitals.model.Appointment;
import com.flmhospitals.model.Diagnosis;
import com.flmhospitals.service.AppointmentService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientClient patientClient;
	private final NotificationClient notificationClient;
	private final DiagnosisRepository diagnosisRepository;
	private final DoctorClient doctorClient;
	
	public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PatientClient patientClient, NotificationClient notificationClient, DiagnosisRepository diagnosisRepository
			,DoctorClient doctorClient) {
		this.appointmentRepository = appointmentRepository;
		this.patientClient = patientClient;
		this.notificationClient = notificationClient;
		this.diagnosisRepository = diagnosisRepository;
		this.doctorClient = doctorClient;
	}
	
	@Override
	public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO appointmentRequestDTO)  {
		
		if(appointmentRequestDTO.getStartTime().isAfter(appointmentRequestDTO.getEndTime()) ||
				appointmentRequestDTO.getStartTime().equals(appointmentRequestDTO.getEndTime())) {
			throw new InvalidTimeException("Start time must be before end time");
		}
		
		if(appointmentRequestDTO.getAppointmentDate().isBefore(LocalDate.now())) {
			throw new InvalidTimeException("Cannot book appointments for past dates. Please select a future date.");
		}
		
		LocalDate appointmentDate = appointmentRequestDTO.getAppointmentDate();
		String date = appointmentDate.toString();
		
		List<Appointment> doctorAppointments = appointmentRepository.findAppointmentsByDoctorId(
				appointmentRequestDTO.getDoctorId(), appointmentRequestDTO.getAppointmentDate(),
				appointmentRequestDTO.getStartTime(), appointmentRequestDTO.getEndTime());
		
		if(!doctorAppointments.isEmpty()) {
			throw new AppointmentAlreadyExistsException(
					"Doctor already has an appointment during this time slot. Please choose a different time.");
		}
		
		List<Appointment> patientAppointments = appointmentRepository.findByPatientId(
				appointmentRequestDTO.getPatientId(), appointmentRequestDTO.getAppointmentDate(),
				appointmentRequestDTO.getStartTime(),appointmentRequestDTO.getEndTime());
		
		if(!patientAppointments.isEmpty()) {
			throw new AppointmentAlreadyExistsException(
					"Patient already has an appointment during this time slot. Please choose a different time slot.");
		}
		
		Appointment appointment = AppointmentBuilder.buildAppointmentFromAppointmentRequestDTO(appointmentRequestDTO);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		sendAppointmentNotification(savedAppointment);
		
		return getAppointmentDetails(savedAppointment.getAppointmentId());
		
	}
	
	private void sendAppointmentNotification(Appointment savedAppointment) {
		  ResponseEntity<RegisterPatientResponseDto> patientDetails = patientClient.getPatientById(savedAppointment.getPatientId());
		  
		  if(patientDetails != null && patientDetails.getBody() != null) {
			RegisterPatientResponseDto patient = patientDetails.getBody();
			
			if(patient.getPatientEmail() != null && !patient.getPatientEmail().isEmpty()) {
				EmailRequestDto emailRequest = new EmailRequestDto();
				emailRequest.setTo(patient.getPatientEmail());
				emailRequest.setSubject("Appointment Conformation - MedSync");
				emailRequest.setBody(
						"<h2>Dear " + patient.getPatientName() + ",</h2>"
						        + "<p>Your appointment has been successfully scheduled.</p>"
						        + "<p><b>Appointment ID:</b> " + savedAppointment.getAppointmentId() + "</p>"
						        + "<p><b>Date :</b> " + savedAppointment.getAppointmentDate()+ "</p>"
						        + "<p><b>Time :</b> " + savedAppointment.getStartTime() +" - "+savedAppointment.getEndTime() + "</p>"
						        + "<p>Thank you for choosing MedSync.</p>"
						        );
				notificationClient.sendEmail(emailRequest);	
			}
			
		  }
	}
	
	@Override
	public AppointmentResponseDTO getAppointmentDetails(String appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
		.orElseThrow(() -> new AppointmentNotFoundException("no appointment found with Id : " + appointmentId));

		Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId).orElse(null);
		AppointmentResponseDTO appointmentResponseDTO = AppointmentDTOBuilder.buildAppointmentResponseDTO(appointment,diagnosis);
		
		try {
			String doctorName = doctorClient.getDoctorName(appointment.getDoctorId());
			appointmentResponseDTO.setDoctorName(doctorName);
		}catch (Exception e){
			log.warn("Failed to fetch doctor name : {}",e.getMessage());
		}
		
		try {
			String patientName = patientClient.getPatientName(appointment.getPatientId());
			appointmentResponseDTO.setPatientName(patientName);
			
		}catch(Exception e) {
			log.warn("Failed to fetch patient name : {}",e.getMessage());
		}
		
		return appointmentResponseDTO;
	}

	@Override
	public List<AppointmentResponseDTO> getAllAppointmentsForAllDoctors(LocalDate date) {
		List<Appointment> appointments = appointmentRepository.findByAppointmentDate(date);
		return buildResponseList(appointments);
	}

	private List<AppointmentResponseDTO> buildResponseList(List<Appointment> appointments) {

		Map<String, String> doctorNameCache = new HashMap<>();
		Map<String, String> patientNameCache = new HashMap<>();
		
		return appointments.stream().map(appointment ->{
			Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointment.getAppointmentId()).orElse(null);
			AppointmentResponseDTO dto = AppointmentDTOBuilder.buildAppointmentResponseDTO(appointment, diagnosis);
			
			String doctorName = doctorNameCache.computeIfAbsent(appointment.getDoctorId(),  id ->{
				try {
					return doctorClient.getDoctorName(id);
				}catch(Exception e) { log.warn("Failed to fetch doctor name for {}: {}",id,e.getMessage());
				return null;
				}
			});
			
			if (doctorName != null) dto.setDoctorName(doctorName);

			String patientName = patientNameCache.computeIfAbsent(appointment.getPatientId(), id -> {
				try { return patientClient.getPatientName(id); }
				catch (Exception e) { log.warn("Failed to fetch patient name for {}: {}", id, e.getMessage()); return null; }
			});
			if (patientName != null) dto.setPatientName(patientName);

			return dto;
			
		}).toList();
							
				
	}

	@Override
	public List<AppointmentResponseDTO> getAllAppointmentsOfDoctor(String doctorId, LocalDate date) {
		List<Appointment> appointments = appointmentRepository.findByAppointmentDate(date);
		return buildResponseList(appointments);
	}

	@Override
	public List<String> getPatientByDoctor(String staffId, LocalDate startDate, LocalDate endDate) {	
		return appointmentRepository.findByPatientsByStaffId(staffId,startDate,endDate);
	}

	@Override
	@Transactional
	public boolean cancelAppointment(String appointmentId) {
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
								.orElseThrow(() -> new AppointmentNotFoundException("Appointment with Id :"+ appointmentId + " not found"));
		
		if(appointment.getStatus().equals("CANCELLED")) {
			throw new IllegalStateException("Appointment is already cancelled");
		}
		
		appointment.setStatus("CANCELLED");
		appointmentRepository.save(appointment);
		
		try {
			appointmentCancelledNotification(appointment);
		}catch(Exception e) {
			log.error("Failed to send cancelled notification : {}",e.getMessage());
		}
		
		return true;
	}

	private void appointmentCancelledNotification(Appointment appointment) {
		
		ResponseEntity<RegisterPatientResponseDto> patients = patientClient.getPatientById(appointment.getPatientId());
		
		if(patients != null && patients.getBody() != null) {
			RegisterPatientResponseDto patient = patients.getBody();
			
			if(patient.getPatientEmail() != null && !patient.getPatientEmail().isEmpty()) {
				EmailRequestDto emailRequest = new EmailRequestDto();
				emailRequest.setTo(patient.getPatientEmail());
				emailRequest.setSubject("Appointment Cancelled - MedSync");
				emailRequest.setBody(
						"<h2>Dear " + patient.getPatientName() + ",</h2>"
						        + "<p>Your appointment has been <b>successfully cancelled</b>.</p>"
						        + "<p><b>Appointment ID:</b> " + appointment.getAppointmentId() + "</p>"
						        + "<p>Thank you for choosing MedSync.</p>"
						);
				notificationClient.sendEmail(emailRequest);
			}
			
		}
		
	}

	@Override
	public List<AppointmentResponseDTO> getAllFutureAppointmentsOfDoctor(String doctorId) {
		List<Appointment> list = appointmentRepository.findByDoctorId(doctorId)
								.stream()
								.filter(appointment -> appointment.getAppointmentDate().isAfter(LocalDate.now()))
								.toList();
		
		return buildResponseList(list);
								
	}

	@Override
	public AppointmentResponseDTO rescheduleAppointment(String appointmentId,
			RescheduleAppointmentDTO rescheduleAppointmentDTO) {
		
		if(rescheduleAppointmentDTO.getNewStartTime().isAfter(rescheduleAppointmentDTO.getNewEndTime()) ||
			rescheduleAppointmentDTO.getNewStartTime().equals(rescheduleAppointmentDTO.getNewEndTime())) {
			throw new InvalidTimeException("Start time must be before end time");
		}
		
		if(rescheduleAppointmentDTO.getNewDate().isBefore(LocalDate.now())) {
			throw new InvalidTimeException("Cannot reschedule appointment to past dates");
		}
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
								.orElseThrow(() -> new AppointmentNotFoundException("Appointment with Id "+appointmentId + " not found") );
		
		
		appointment.setAppointmentDate(rescheduleAppointmentDTO.getNewDate());
		appointment.setStartTime(rescheduleAppointmentDTO.getNewStartTime());
		appointment.setEndTime(rescheduleAppointmentDTO.getNewEndTime());
		
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		try {
			sendRescheduleAppointmentNotification(savedAppointment);
		}catch(Exception e) {
			log.error("Failed to send reschedule notification : {}",e.getMessage());
		}
		return getAppointmentDetails(savedAppointment.getAppointmentId());
	}

	private void sendRescheduleAppointmentNotification(Appointment savedAppointment) {
		ResponseEntity<RegisterPatientResponseDto> patientDetails = patientClient.getPatientById(savedAppointment.getPatientId());
		
		if(patientDetails != null && patientDetails.getBody() != null) {
			RegisterPatientResponseDto patient = patientDetails.getBody();
			if(patient.getPatientEmail() != null && !patient.getPatientEmail().isEmpty()) {
				EmailRequestDto emailRequest = new EmailRequestDto();
				emailRequest.setTo(patient.getPatientEmail());
				emailRequest.setSubject("Appointment Rescheduled - MedSync");
				emailRequest.setBody(
						 "<h2>Dear " + patient.getPatientName() + ",</h2>"
							        + "<p>Your appointment has been <b>successfully rescheduled</b>.</p>"
							        + "<p><b>Appointment ID:</b> " + savedAppointment.getAppointmentId() + "</p>"
							        + "<p><b>New Date :</b> " + savedAppointment.getAppointmentDate() + "</p>"
							        + "<p><b>New Time :</b> " + savedAppointment.getStartTime() +" - "+savedAppointment.getEndTime() + "</p>"
							        + "<p>Thank you for choosing MedSync.</p>"
							        );
				notificationClient.sendEmail(emailRequest);
			}
		}	
	}

	@Override
	@Transactional
	public AppointmentResponseDTO submitDiagnosis(String appointmentId, DiagnosisRequestDTO diagnosisRequestDTO) {

		Appointment appointment = appointmentRepository.findById(appointmentId)
								.orElseThrow(() -> new AppointmentNotFoundException("Appointment with Id "+appointmentId + " not found"));
		
		Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
							.orElse(new Diagnosis());
		
		diagnosis.setAppointmentId(appointmentId);
		diagnosis.setDiagnosisSummary(diagnosisRequestDTO.getDiagnosisSummary());
		diagnosis.setPrescription(diagnosisRequestDTO.getPrescription());
		diagnosis.setNotesForReceptionist(diagnosis.getNotesForReceptionist());
		diagnosis.setFollowUpSuggestion(diagnosisRequestDTO.getFollowUpSuggestion());
		
		diagnosisRepository.save(diagnosis);
		
		appointment.setStatus("DIAGNOSED");
		appointmentRepository.save(appointment);
		
		return getAppointmentDetails(appointmentId);
	}
	
	
	

	

}
