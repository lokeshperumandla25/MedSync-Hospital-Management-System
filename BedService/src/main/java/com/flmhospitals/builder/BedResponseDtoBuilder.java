package com.flmhospitals.builder;

import com.flmhospitals.dto.BedDetailsResponseDTO;
import com.flmhospitals.model.Bed;

public class BedResponseDtoBuilder {

	public static BedDetailsResponseDTO buildBedDetailsResponseDtoFromBed(Bed bed) {
		
		return BedDetailsResponseDTO.builder()
						.bedNumber(bed.getBedNumber())
						.roomNumber(bed.getRoom().getRoomNumber())
						.isOccupied(bed.isOccupied())
						.build();
	}

}
