package com.flmhospitals.utils;

import org.springframework.stereotype.Component;

import com.flmhospitals.dao.StaffRepository;

@Component
public class StaffIdGenerator {
	
	private final StaffRepository repository;
	
	public StaffIdGenerator(StaffRepository repository) {
		this.repository = repository;
	}
	
	public String generateNextStaffId() {
		String lastId = repository.findLastStaffId();
		int nextNumber = 1;
		
		if(lastId != null && lastId.startsWith("FLM-")) {
			String numberPart = lastId.substring(4);
			nextNumber = Integer.parseInt(numberPart)+1;
			return String.format("FLM-%05d", nextNumber);
		}
		
		return String.format("FLM-%05d", nextNumber);
		
	}
	
	

}
