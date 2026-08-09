package com.flmhospitals.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flmhospitals.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{

	Room findByRoomNumber(long roomNumber);

}
