package com.flmhospitals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedRequestDTO {
	
	private long bedNumber;
	
	private long roomNumber;
	
	@JsonProperty("occupied")
	private boolean isOccupied;

}
