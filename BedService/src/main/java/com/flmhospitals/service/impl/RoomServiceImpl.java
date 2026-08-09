package com.flmhospitals.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.flmhospitals.builder.BedBuilder;
import com.flmhospitals.builder.RoomBuilder;
import com.flmhospitals.builder.RoomDTOBuilder;
import com.flmhospitals.dao.BedAssignmentHistoryRepository;
import com.flmhospitals.dao.RoomRepository;
import com.flmhospitals.dto.BedRequestDTO;
import com.flmhospitals.dto.RoomRequestDto;
import com.flmhospitals.dto.RoomResponseDto;
import com.flmhospitals.exception.RoomNotFoundException;
import com.flmhospitals.model.Bed;
import com.flmhospitals.model.Room;
import com.flmhospitals.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

//    private final BedAssignmentHistoryRepository bedAssignmentHistoryRepository;
	
	private final RoomRepository roomRepository;
	
	public RoomServiceImpl(RoomRepository roomRepository, BedAssignmentHistoryRepository bedAssignmentHistoryRepository) {
		this.roomRepository = roomRepository;
//		this.bedAssignmentHistoryRepository = bedAssignmentHistoryRepository;
	}

	@Override
	public RoomResponseDto addRoom(RoomRequestDto roomRequestDto) {
		
		Room room = RoomBuilder.buildRoomFromRoomDTO(roomRequestDto);
		if(roomRequestDto.getBeds() != null) {
			List<Bed> bedEntities = new ArrayList<>();
			for(BedRequestDTO dto:roomRequestDto.getBeds()) {
				 Bed bed = BedBuilder.buildBedFromBedRequestDto(dto);
				 bedEntities.add(bed);
				 bed.setRoom(room);
			}
			room.setBeds(bedEntities);	
		}
		
		Room savedRoom = roomRepository.save(room);
		
		return RoomDTOBuilder.buildRoomResponseFromRoom(savedRoom);
	}

	@Override
	public boolean removeRoom(long roomNumber) {
		
		Room room = roomRepository.findById(roomNumber)
						.orElseThrow(() -> new RoomNotFoundException("Room not found with "+roomNumber));
		
		roomRepository.delete(room);
		return true;
	}

	@Override
	public RoomResponseDto updateRoomDetails(long roomNumber, RoomRequestDto roomRequestDto) {
		Room existingRoom = roomRepository.findById(roomNumber)
						.orElseThrow(() -> new RoomNotFoundException("Room Not Found with Room Number "+roomNumber));
		
		int currentBedCount = existingRoom.getBeds() != null ? existingRoom.getBeds().size() : 0;
		if(roomRequestDto.getRoomCapacity() < currentBedCount) {
			throw new IllegalArgumentException("Cannot reduce capacity to "+ roomRequestDto.getRoomCapacity() +
					" as room currently has "+ currentBedCount + " beds");
		}
		
		existingRoom.setRoomType(roomRequestDto.getRoomType());
		existingRoom.setRoomCapacity(roomRequestDto.getRoomCapacity());
		
		Room room = roomRepository.save(existingRoom);
		
		return RoomDTOBuilder.buildRoomResponseFromRoom(room);
	}

	@Override
	public List<RoomResponseDto> getAllRoomDetails() {
		List<Room> rooms = roomRepository.findAll();
		
		List<RoomResponseDto> roomResponse = new ArrayList<>();
		for(Room room : rooms) {
			RoomResponseDto roomResponseFromRoom = RoomDTOBuilder.buildRoomResponseFromRoom(room);
			roomResponse.add(roomResponseFromRoom);
		}
		
		return roomResponse;
	}

}
