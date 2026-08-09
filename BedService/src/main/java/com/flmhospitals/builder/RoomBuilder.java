package com.flmhospitals.builder;

import java.util.ArrayList;
import java.util.List;

import com.flmhospitals.dto.BedRequestDTO;
import com.flmhospitals.dto.RoomRequestDto;
import com.flmhospitals.model.Bed;
import com.flmhospitals.model.Room;

public class RoomBuilder {

	public static Room buildRoomFromRoomDTO(RoomRequestDto roomRequestDto) {
		
		return Room.builder()
			.roomNumber(roomRequestDto.getRoomNumber())
			.roomType(roomRequestDto.getRoomType())
			.roomCapacity(roomRequestDto.getRoomCapacity())
			.beds(buildBedFromDTO(roomRequestDto.getBeds()))
			.build();
	}

	private static List<Bed> buildBedFromDTO(List<BedRequestDTO> bedRequestDTO) {
		
		List<Bed> bedList = new ArrayList<>();
		
		if(bedRequestDTO != null) {
		for(BedRequestDTO beds : bedRequestDTO ) {
			Bed bed = new Bed();
			bed.setBedNumber(beds.getBedNumber());
			bed.setOccupied(beds.isOccpied());
			bedList.add(bed);
		}
	}
		
		return bedList;
	}

}
