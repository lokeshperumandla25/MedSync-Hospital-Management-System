package com.flmhospitals.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flmhospitals.model.BedAssignmentHistory;

@Repository
public interface BedAssignmentHistoryRepository extends JpaRepository<BedAssignmentHistory, Long> {

	BedAssignmentHistory findTopByBed_BedNumberAndVacatedAtIsNullOrderByAssignedAtDesc(long bedNumber);

	List<BedAssignmentHistory> findByBed_BedNumber(long bedNumber);

}
