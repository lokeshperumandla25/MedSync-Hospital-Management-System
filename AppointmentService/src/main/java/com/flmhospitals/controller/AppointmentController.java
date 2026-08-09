package com.flmhospitals.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.dto.DiagnosisRequestDTO;
import com.flmhospitals.dto.RescheduleAppointmentDTO;
import com.flmhospitals.model.Appointment;
import com.flmhospitals.service.AppointmentService;

import jakarta.ws.rs.Path;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;
	
	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}
	
	@PostMapping("/bookAppointment")
	public ResponseEntity<AppointmentResponseDTO> bookAppointment(
			@RequestBody AppointmentRequestDTO appointmentRequestDTO){
		
		AppointmentResponseDTO responseDTO = appointmentService.bookAppointment(appointmentRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
	}
	
	@GetMapping("/{date}")
	public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointmentsForAllDoctors(@PathVariable(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
		return ResponseEntity.ok(appointmentService.getAllAppointmentsForAllDoctors(date));
	}
	
	@GetMapping("/{doctorId}/{date}")
	public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointmentsOfDoctor(@PathVariable("doctorId") String doctorId,@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
		return ResponseEntity.ok(appointmentService.getAllAppointmentsOfDoctor(doctorId,date));
	}
	
	@GetMapping("/getDoctorPatients/{staffId}")
	public List<String> getPatientsVisitedByDoctor(@PathVariable("staffId") String staffId,@RequestParam("startDate") String startdate,@RequestParam("endDate") String enddate){
		LocalDate startDate = LocalDate.parse(startdate);
		LocalDate endDate = LocalDate.parse(enddate);
		
		return appointmentService.getPatientByDoctor(staffId,startDate,endDate);
	}
 	
	@DeleteMapping("/{appointmentId}")
	public ResponseEntity<String> cancelAppointment(@PathVariable("appointmentId") String appointmentId){
		appointmentService.cancelAppointment(appointmentId);
		return ResponseEntity.ok("Appointment cancelled successfully");
	}
	
	@GetMapping("/allFutureAppointments/{doctorId}")
	public ResponseEntity<List<AppointmentResponseDTO>> getAllFutureAppointmentsOfDoctor(@PathVariable("doctorId") String doctorId){
		return ResponseEntity.ok(appointmentService.getAllFutureAppointmentsOfDoctor(doctorId));
	}
	
	@PutMapping("/resheduleAppointmentDetails/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> reScheduleAppointment(@PathVariable("appointmentId") String appointmentId ,@RequestBody RescheduleAppointmentDTO rescheduleAppointmentDTO){
		AppointmentResponseDTO responseDTO = appointmentService.rescheduleAppointment(appointmentId,rescheduleAppointmentDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
	}
	
	@GetMapping("/getAppointmentDetails/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> getAppointmentDetails(@PathVariable(name = "appointmentId") String appointmentId){
		return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAppointmentDetails(appointmentId));
	}
	
	@PostMapping("/submitDiagnosis/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> submitDiagnosis(@PathVariable(name = "appointmentId") String appointmentId,
			@RequestBody DiagnosisRequestDTO diagnosisRequestDTO){
		
		AppointmentResponseDTO submitDiagnosis = appointmentService.submitDiagnosis(appointmentId, diagnosisRequestDTO);
		
		return ResponseEntity.status(HttpStatus.OK).body(submitDiagnosis);
		
	}
	
	
}
