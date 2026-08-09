package com.flmhospitals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(StaffNotFoundException.class)
	public ResponseEntity<String> getStaffNotFoundException(StaffNotFoundException exception){
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<String> handleInvalidOtp(InvalidOtpException exception){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage()) ;
	}
	
	@ExceptionHandler(DoctorNotFoundException.class)
	public ResponseEntity<String> getDoctorNotFoundException(DoctorNotFoundException exception){
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

}
