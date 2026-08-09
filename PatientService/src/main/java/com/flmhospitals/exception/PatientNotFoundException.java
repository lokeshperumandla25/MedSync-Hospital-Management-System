package com.flmhospitals.exception;

public class PatientNotFoundException extends RuntimeException{

	public PatientNotFoundException(String Message) {
		super(Message);
	}
}
