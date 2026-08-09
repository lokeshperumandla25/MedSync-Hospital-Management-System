package com.flmhospitals.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flmhospitals.dto.BedDetailsResponseDTO;
import com.flmhospitals.dto.BedRequestDTO;
import com.flmhospitals.service.BedService;

@RestController
@RequestMapping("/bed")
public class BedController {

	private final BedService bedService;
	
	public BedController(BedService bedService) {
		this.bedService = bedService;
	}
	
	@PostMapping("/addBed")
	public ResponseEntity<String> addBedInRoom(@RequestBody BedRequestDTO bedRequestDTO) {
		return bedService.addBedInRoom(bedRequestDTO);
	}
	
	@DeleteMapping("/delete-bed/{bedNumber}/{roomNumber}")
	public ResponseEntity<String> removeBed(@PathVariable("bedNumber") long bedNumber,@PathVariable("roomNumber") long roomNumber){
		return bedService.removeBed(bedNumber,roomNumber);
	}
	
	@GetMapping("/getBeds")
	public ResponseEntity<List<BedDetailsResponseDTO>> getAllBedDetails(){
		List<BedDetailsResponseDTO> allBedDetails = bedService.getAllBedDetails();
		return ResponseEntity.status(HttpStatus.OK).body(allBedDetails);
	}
	
	@GetMapping("/room/{roomNumber}")
	public ResponseEntity<List<BedDetailsResponseDTO>> getAllBedsInRoom(@PathVariable("roomNumber") long roomNumber){
		List<BedDetailsResponseDTO> detailsResponseDTOs = bedService.getBedsByRoomId(roomNumber);
		return ResponseEntity.ok(detailsResponseDTOs);
	}
	
	@GetMapping("/vacant/room/{roomNumber}")
	public ResponseEntity<List<BedDetailsResponseDTO>> getVacantBeds(@PathVariable("roomNumber") long roomNumber){
		return ResponseEntity.ok(bedService.getVacantBedsByRoomNumber(roomNumber));
	}
	
	@PutMapping("/update-bed/{roomNumber}/{bedNumber}")
	public ResponseEntity<BedDetailsResponseDTO> updateBedDetails(@PathVariable("roomNumber") long roomNumber,@PathVariable("bedNumber") long bedNumber,@RequestBody BedRequestDTO bedRequestDTO){
		
		ResponseEntity<BedDetailsResponseDTO> bedDetails = bedService.updateBedDetails(roomNumber,bedNumber,bedRequestDTO);
		return bedDetails;
		
	}
	
	
	
	
	
	
}
