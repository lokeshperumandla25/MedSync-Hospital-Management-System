package com.flmhospitals.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.flmhospitals.builder.BedAssignmentHistoryBuilder;
import com.flmhospitals.dao.BedAssignmentHistoryRepository;
import com.flmhospitals.dao.BedRepository;
import com.flmhospitals.dto.BedAssignmentHistoryDTO;
import com.flmhospitals.exception.BedNotFoundException;
import com.flmhospitals.exception.BedUnavailableException;
import com.flmhospitals.model.Bed;
import com.flmhospitals.model.BedAssignmentHistory;
import com.flmhospitals.service.BedAssignmentService;

@Service
public class BedAssignmentServiceImpl implements BedAssignmentService{

	private final BedRepository bedRepository;
	private final BedAssignmentHistoryRepository assignmentHistoryRepository;
	
	public BedAssignmentServiceImpl(BedRepository bedRepository, BedAssignmentHistoryRepository assignmentHistoryRepository) {
		this.bedRepository = bedRepository;
		this.assignmentHistoryRepository = assignmentHistoryRepository;
		
	}
	
	@Override
	public Bed bedAssignment(long bedNumber, String patientId) {
		
		Bed bed = bedRepository.findById(bedNumber)
						.orElseThrow(() -> new BedNotFoundException("Bed not found"));
		
		if(bed.isOccupied()) {
			throw new BedUnavailableException("Bed " + bedNumber + " is already occupied");
		}
		
		bed.setOccupied(true);
		bed.setPatientId(Long.parseLong(patientId));
		bedRepository.save(bed);
		
		BedAssignmentHistory bedHistory = new BedAssignmentHistory();
		bedHistory.setBed(bed);
		bedHistory.setPatienId(Long.parseLong(patientId));
		bedHistory.setAssignedAt(LocalDateTime.now());
		
		assignmentHistoryRepository.save(bedHistory);
		
		return bed;
	}

	@Override
	public Bed vacateBed(long roomNumber, long bedNumber) {
		
		Bed bed = bedRepository.findBedWithRoomNumber(bedNumber, roomNumber)
						.orElseThrow(() -> new BedNotFoundException("Bed Not Found with BedNumber "+bedNumber+" in RoomNumber: "+roomNumber));
		
		BedAssignmentHistory activeAssignment = assignmentHistoryRepository.findTopByBed_BedNumberAndVacatedAtIsNullOrderByAssignedAtDesc(bedNumber);
		
		if(activeAssignment == null) {
			throw new IllegalStateException("No active bed assignment found for bed " + bedNumber);
		}
		
		activeAssignment.setVacatedAt(LocalDateTime.now());
		
		bed.setOccupied(false);
		bed.setPatientId(0);
		bedRepository.save(bed);
		assignmentHistoryRepository.save(activeAssignment);
		
		return bed;
	}

	@Override
	public List<BedAssignmentHistoryDTO> getHistoryByBedNumber(long bedNumber) {
		
		List<BedAssignmentHistory> bedHistory = assignmentHistoryRepository.findByBed_BedNumber(bedNumber);
		List<BedAssignmentHistoryDTO> bedHistoryDto = BedAssignmentHistoryBuilder.buildBedHistoryDTOFromBedHistory(bedHistory);
		
		return bedHistoryDto ;
	}

}
