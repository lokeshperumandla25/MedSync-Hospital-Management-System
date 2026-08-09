package com.flmhospitals.service;

import java.util.List;

import com.flmhospitals.dto.RoomRequestDto;
import com.flmhospitals.dto.RoomResponseDto;

public interface RoomService {

	RoomResponseDto addRoom(RoomRequestDto roomRequestDto);

	boolean removeRoom(long roomNumber);

	RoomResponseDto updateRoomDetails(long roomNumber, RoomRequestDto roomRequestDto);

	List<RoomResponseDto> getAllRoomDetails();


}
