package com.flmhospitals.builder;

import java.util.ArrayList;
import java.util.List;

import com.flmhospitals.dto.BedDetailsResponseDTO;
import com.flmhospitals.dto.RoomResponseDto;
import com.flmhospitals.model.Bed;
import com.flmhospitals.model.Room;

public class RoomDTOBuilder {

	public static RoomResponseDto buildRoomResponseFromRoom(Room room) {
		
		return RoomResponseDto.builder()
					.roomNumber(room.getRoomNumber())
					.roomType(room.getRoomType())
					.roomCapacity(room.getRoomCapacity())
					.beds(buildBedDetailsResponseDto(room.getBeds()))
					.build();
			
	}

	private static List<BedDetailsResponseDTO> buildBedDetailsResponseDto(List<Bed> beds) {
		
		List<BedDetailsResponseDTO> bedDetailsResponseDTO = new ArrayList<>();
		
		for(Bed bed : beds) {
			bedDetailsResponseDTO.add(BedResponseDtoBuilder.buildBedDetailsResponseDtoFromBed(bed));
		}
		
		return bedDetailsResponseDTO;
	}
	
	

}
