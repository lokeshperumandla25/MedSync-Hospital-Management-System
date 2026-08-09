package com.flmhospitals.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "beds")
@Builder
public class Bed {
	
	@Id
	private long bedNumber;
	
	private boolean isOccupied;
	
	private long patientId;
	
	@ManyToOne
	@JoinColumn(name = "room_number")
	private Room room;
	
	@OneToMany(mappedBy = "bed",cascade = CascadeType.ALL)
	private List<BedAssignmentHistory> bedAssignmentHistoryList;

	public Bed(boolean isOccupied, long patientId, Room room, List<BedAssignmentHistory> bedAssignmentHistoryList) {
		super();
		this.isOccupied = isOccupied;
		this.patientId = patientId;
		this.room = room;
		this.bedAssignmentHistoryList = bedAssignmentHistoryList;
	}
	
	
	
	
	
	

}
