package com.flmhospitals.builder;

import java.util.List;

import com.flmhospitals.dto.BedAssignmentHistoryDTO;
import com.flmhospitals.model.BedAssignmentHistory;

public class BedAssignmentHistoryBuilder {

	public static List<BedAssignmentHistoryDTO> buildBedHistoryDTOFromBedHistory(List<BedAssignmentHistory> bedHistory) {
		
		return bedHistory.stream()
						.map(history -> BedAssignmentHistoryDTO.builder()
													.BedAssignmentHistoryId(history.getBedAssignmentHistoryId())
													.bedNumber(history.getBed().getBedNumber())
													.assignedAt(history.getAssignedAt())
													.vacatedAt(history.getVacatedAt())
													.patientId(history.getPatienId())
													.build()).toList();
		
	}


}
