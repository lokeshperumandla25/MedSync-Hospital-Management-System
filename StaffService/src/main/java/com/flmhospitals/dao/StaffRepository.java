package com.flmhospitals.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flmhospitals.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, String>{

	Optional<Staff> findByEmail(String email);

	Optional<Staff> findByPhoneNumber(String phoneNumber);
	
	@Query(value = "SELECT staff_id from staff where staff_id LIKE 'FLM-%' ORDER BY staff_id DESC LIMIT 1",nativeQuery = true)
	String findLastStaffId();

	List<Staff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, String name2);


}
