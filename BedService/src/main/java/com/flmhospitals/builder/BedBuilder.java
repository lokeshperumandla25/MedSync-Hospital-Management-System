package com.flmhospitals.builder;

import com.flmhospitals.dto.BedRequestDTO;
import com.flmhospitals.model.Bed;

public class BedBuilder {

	public static Bed buildBedFromBedRequestDto(BedRequestDTO dto) {

		return Bed.builder()
				.bedNumber(dto.getBedNumber())
				.isOccupied(dto.isOccpied())
				.build();
		
	}
	
	

}
