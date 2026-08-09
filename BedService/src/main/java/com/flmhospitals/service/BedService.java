package com.flmhospitals.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.flmhospitals.dto.BedDetailsResponseDTO;
import com.flmhospitals.dto.BedRequestDTO;

public interface BedService {

	ResponseEntity<String> addBedInRoom(BedRequestDTO bedRequestDTO);

	ResponseEntity<String> removeBed(long bedNumber, long roomNumber);

	List<BedDetailsResponseDTO> getAllBedDetails();

	List<BedDetailsResponseDTO> getBedsByRoomId(long roomNumber);

	List<BedDetailsResponseDTO> getVacantBedsByRoomNumber(long roomNumber);

	ResponseEntity<BedDetailsResponseDTO> updateBedDetails(long roomNumber, long bedNumber, BedRequestDTO bedRequestDTO);

}
