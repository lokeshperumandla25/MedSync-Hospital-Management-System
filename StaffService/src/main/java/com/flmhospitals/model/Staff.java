package com.flmhospitals.model;

import java.time.LocalDate;

import org.hibernate.annotations.Collate;

import com.flmhospitals.enums.Specialization;
import com.flmhospitals.enums.StaffType;
import com.flmhospitals.utils.StaffEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "staff")
@Builder
@EntityListeners(StaffEntityListener.class)
public class Staff {
	
	@Id
	@Column(name = "staff_id", nullable = false,unique = true)
	private String staffId;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Column(nullable = false,length = 15)
	private String phoneNumber;
	
	@Column(nullable = false)
	private String gender;
	
	@Column(nullable = false)
	private String role;
	
	@Column(nullable = false)
	private LocalDate dateOfJoining;
	
	@Column(nullable = false)
	private int experienceInYears;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private boolean canLogin;
			
	@Column(nullable = false)
	private boolean isEmployeeActive;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "staffAddressId")
	private StaffAddress staffAddress;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "staffDetailsId")
	private StaffDetails staffDetails;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StaffType staffType;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Specialization specialization;
	
	


}
