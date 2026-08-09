package com.flmhospitals.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BedAssignmentHistoryDTO {
	
	private long BedAssignmentHistoryId;
	
	private long bedNumber;
	
	private long patientId;
	
	private LocalDateTime assignedAt;
	
	private LocalDateTime vacatedAt;
	

}
