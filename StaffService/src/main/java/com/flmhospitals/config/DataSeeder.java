package com.flmhospitals.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.flmhospitals.dao.StaffRepository;
import com.flmhospitals.enums.Specialization;
import com.flmhospitals.enums.StaffType;
import com.flmhospitals.model.Staff;
import com.flmhospitals.model.StaffAddress;
import com.flmhospitals.model.StaffDetails;

@Component
public class DataSeeder implements CommandLineRunner {
	
	private final StaffRepository staffRepository;
	private final PasswordEncoder passwordEncoder;
	
	public DataSeeder(StaffRepository staffRepository, PasswordEncoder passwordEncoder){
		this.staffRepository = staffRepository;
		this.passwordEncoder = passwordEncoder;
	}
		
	@Override
	public void run(String... args) throws Exception {
if (staffRepository.findByEmail("admin@flmhospitals.com").isEmpty()) {
            
            // Create super admin address
            StaffAddress adminAddress = StaffAddress.builder()
                    .landmark("Hospital Main Building")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .country("India")
                    .pincode("400001")
                    .build();

            // Create super admin details with hashed password
            StaffDetails adminDetails = StaffDetails.builder()
                    .email("admin@flmhospitals.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .requirePasswordReset(false)
                    .build();

            // Create super admin staff
            Staff superAdmin = Staff.builder()
                    .staffId("ADMIN001")
                    .firstName("Super")
                    .lastName("Admin")
                    .gender("Male")
                    .phoneNumber("9999999999")
                    .staffType(StaffType.NON_DOCTOR)
                    .role("ADMIN")
                    .specialization(Specialization.OTHERS)
                    .dateOfJoining(LocalDate.now())
                    .experienceInYears(10)
                    .email("admin@flmhospitals.com")
                    .canLogin(true)
                    .isEmployeeActive(true)
                    .staffAddress(adminAddress)
                    .staffDetails(adminDetails)
                    .build();

            staffRepository.save(superAdmin);
            
            System.out.println("========================================");
            System.out.println("Super Admin Created Successfully!");
            System.out.println("Email: admin@flmhospitals.com");
            System.out.println("Password: Admin@123");
            System.out.println("Staff ID: ADMIN001");
            System.out.println("========================================");
        }
        
        // Check if receptionist already exists
        if (staffRepository.findByEmail("receptionist@flmhospitals.com").isEmpty()) {
            
            // Create receptionist address
            StaffAddress receptionistAddress = StaffAddress.builder()
                    .landmark("Hospital Reception")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .country("India")
                    .pincode("400001")
                    .build();

            // Create receptionist details with hashed password
            StaffDetails receptionistDetails = StaffDetails.builder()
                    .email("receptionist@flmhospitals.com")
                    .password(passwordEncoder.encode("Receptionist@123"))
                    .requirePasswordReset(false)
                    .build();

            // Create receptionist staff
            Staff receptionist = Staff.builder()
                    .staffId("REC001")
                    .firstName("Front")
                    .lastName("Desk")
                    .gender("Female")
                    .phoneNumber("9999999998")
                    .staffType(StaffType.NON_DOCTOR)
                    .role("RECEPTIONIST")
                    .specialization(Specialization.RECEPTIONIST)
                    .dateOfJoining(LocalDate.now())
                    .experienceInYears(2)
                    .email("receptionist@flmhospitals.com")
                    .canLogin(true)
                    .isEmployeeActive(true)
                    .staffAddress(receptionistAddress)
                    .staffDetails(receptionistDetails)
                    .build();

            staffRepository.save(receptionist);
            
            System.out.println("========================================");
            System.out.println("Receptionist Created Successfully!");
            System.out.println("Email: receptionist@flmhospitals.com");
            System.out.println("Password: Receptionist@123");
            System.out.println("Staff ID: REC001");
            System.out.println("========================================");
        }
    }

	}
