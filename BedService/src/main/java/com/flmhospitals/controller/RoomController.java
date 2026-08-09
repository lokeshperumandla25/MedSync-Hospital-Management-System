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

import com.flmhospitals.dto.RoomRequestDto;
import com.flmhospitals.dto.RoomResponseDto;
import com.flmhospitals.service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomController {
	
	private final RoomService roomService;
	
	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}
	
	@PostMapping("/addroom")
	public ResponseEntity<RoomResponseDto> addRoom(@RequestBody RoomRequestDto roomRequestDto){
		
		RoomResponseDto roomResponseDto = roomService.addRoom(roomRequestDto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(roomResponseDto);
		
	}
	
	@DeleteMapping("/delete-rooms/{roomNumber}")
	public ResponseEntity<Boolean> removeRoom(@PathVariable("roomNumber") long roomNumber){
		
		return ResponseEntity.ok(roomService.removeRoom(roomNumber));
		
	}
	
	@PutMapping("/update-rooms/{roomNumber}")
	public ResponseEntity<RoomResponseDto> updateRoomDetails(@PathVariable("roomNumber") long roomNumber,
									@RequestBody RoomRequestDto roomRequestDto){
		RoomResponseDto updateRoomDetails = roomService.updateRoomDetails(roomNumber,roomRequestDto);
		return ResponseEntity.ok(updateRoomDetails);
	}
	
	@GetMapping("/getAllRooms")
	public ResponseEntity<List<RoomResponseDto>> getAllRooms(){
		List<RoomResponseDto> responseDtos = roomService.getAllRoomDetails();
		return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
	}

}
