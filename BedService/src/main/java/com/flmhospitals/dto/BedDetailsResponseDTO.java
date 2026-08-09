package com.flmhospitals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BedDetailsResponseDTO {
	
	private long bedNumber;
	
	private long roomNumber;
	
	private boolean isOccupied;
	
}
