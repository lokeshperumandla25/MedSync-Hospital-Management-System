package com.flmhospitals.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flmhospitals.model.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long>{

	Optional<DoctorSchedule> findByStaff_StaffIdAndUnavailableDate(String staffId, LocalDate unvailableDate);

	boolean existsByStaff_StaffIdAndUnavailableDate(String staffId, LocalDate unvailableDate);

}
