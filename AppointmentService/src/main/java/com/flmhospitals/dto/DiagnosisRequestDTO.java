package com.flmhospitals.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisRequestDTO {
	
	private String diagnosisSummary;
	
	private String prescription;
	
	private String medicines;
	
	private String notesForReceptionist;
	
	private String followUpSuggestion;

}
