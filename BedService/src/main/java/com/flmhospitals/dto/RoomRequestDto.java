package com.flmhospitals.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDto {
	
	private long roomNumber;
	
	private String roomType;
	
	private long roomCapacity;
	
	private List<BedRequestDTO> beds;

}
