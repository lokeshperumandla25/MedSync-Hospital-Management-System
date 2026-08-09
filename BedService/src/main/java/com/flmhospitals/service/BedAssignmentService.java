package com.flmhospitals.service;

import java.util.List;

import com.flmhospitals.dto.BedAssignmentHistoryDTO;
import com.flmhospitals.model.Bed;

public interface BedAssignmentService {

	Bed bedAssignment(long bedNumber, String patientId);

	Bed vacateBed(long roomNumber, long bedNumber);

	List<BedAssignmentHistoryDTO> getHistoryByBedNumber(long bedNumber);

}
