package com.flmhospitals.service.Impl;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flmhospitals.builder.StaffBuilder;
import com.flmhospitals.builder.StaffDtoBuilder;
import com.flmhospitals.client.NotificationFeignClient;
import com.flmhospitals.dao.StaffDetailsRepository;
import com.flmhospitals.dao.StaffRepository;
import com.flmhospitals.dto.EmailRequestDto;
import com.flmhospitals.dto.LoginRequest;
import com.flmhospitals.dto.LoginResponse;
import com.flmhospitals.dto.RegisterStaffDto;
import com.flmhospitals.dto.ResetPasswordRequest;
import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.dto.VerifyOtpRequest;
import com.flmhospitals.enums.Specialization;
import com.flmhospitals.enums.StaffType;
import com.flmhospitals.exception.DoctorNotFoundException;
import com.flmhospitals.exception.GlobalExceptionHandler;
import com.flmhospitals.exception.InvalidOtpException;
import com.flmhospitals.exception.StaffNotFoundException;
import com.flmhospitals.model.Staff;
import com.flmhospitals.model.StaffDetails;
import com.flmhospitals.security.JwtService;
import com.flmhospitals.service.StaffService;

import jakarta.transaction.Transactional;

@Service
public class StaffServiceImpl implements StaffService {
	
	private final NotificationFeignClient notificationFeignClient;
	private final StaffRepository staffRepository;
	private final StaffDetailsRepository detailsRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService; 
	
	public StaffServiceImpl(StaffRepository staffRepository, NotificationFeignClient notificationFeignClient, StaffDetailsRepository detailsRepository, PasswordEncoder passwordEncoder, GlobalExceptionHandler globalExceptionHandler, JwtService jwtService) {
		this.staffRepository = staffRepository;
		this.notificationFeignClient = notificationFeignClient;
		this.detailsRepository = detailsRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Override
	public StaffDetailsDto registerStaffDetails(RegisterStaffDto registerStaffDto) {
		if(staffRepository.findByEmail(registerStaffDto.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email "+ registerStaffDto.getEmail() + " is already registered");
		}
		
		if(staffRepository.findByPhoneNumber(registerStaffDto.getPhoneNumber()).isPresent()) {
			throw new IllegalArgumentException("Phone number " + registerStaffDto.getPhoneNumber() +" is already registered");
		}
		
		Staff staff = StaffBuilder.buildStaffFromRegisteredStaffDto(registerStaffDto);
		
		Staff registeredStaff = staffRepository.save(staff);
		
		if(registeredStaff.getStaffType() == StaffType.DOCTOR 
				|| registeredStaff.getSpecialization() == Specialization.RECEPTIONIST) {
			sendNotification(registeredStaff);
		}
		
		return StaffDtoBuilder.buildStaffDetailsDto(registeredStaff);
	}

	private void sendNotification(Staff registeredStaff) {

		String plainPassword = StaffBuilder.getPlainPassword();
		
		String salutation = (registeredStaff.getStaffType() == StaffType.DOCTOR) ? "Dr. " : "";
		EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(registeredStaff.getEmail());
        emailRequest.setSubject("Welcome to MedSync - Temporary Password"); 
        emailRequest.setBody(
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2 style='color: #2c3e50;'>Welcome to MedSync, " + salutation + registeredStaff.getFirstName() + " " + registeredStaff.getLastName() + "</h2>"
                + "<p>Your doctor account has been successfully created by the administrator.</p>"
                + "<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #007bff; margin: 20px 0;'>"
                + "<h3 style='margin-top: 0; color: #007bff;'>Your Login Credentials</h3>"
                + "<p><strong>Username:</strong> " + registeredStaff.getStaffId() + "</p>"
                + "<p><strong>Temporary Password:</strong> " + plainPassword + "</p>"
                + "</div>"
                + "<div style='background-color: #fff3cd; padding: 15px; border-left: 4px solid #ffc107; margin: 20px 0;'>"
                + "<p style='margin: 0;'><strong>⚠️ Important:</strong> For security reasons, you will be required to reset your password upon first login.</p>"
                + "</div>"
                + "<p>Please keep this information secure and do not share it with anyone.</p>"
                + "<p style='margin-top: 30px;'>Best regards,<br/>MedSync Team</p>"
                + "</div>"
                );
        
        try {
            System.out.println("Calling NotificationService to send email...");
            System.out.println("Email request details - To: " + emailRequest.getTo() + ", Subject: " + emailRequest.getSubject());
            String response = notificationFeignClient.sendEmail(emailRequest);
            System.out.println("NotificationService response: " + response);
            System.out.println("Email sent successfully to: " + registeredStaff.getEmail());
        } catch (Exception e) {
            // Log the error but don't fail the registration
            System.err.println("Failed to send notification email to " + registeredStaff.getEmail());
            System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Exception message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Always clear the plain password from ThreadLocal
            StaffBuilder.clearPlainPassword();
        }
        
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		 String normalizedEmail = request.getEmail().trim().toLowerCase();

		    StaffDetails staffDetails = detailsRepository.findByEmail(normalizedEmail)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.UNAUTHORIZED,
		                    "Invalid email or password"
		            ));

		    // Use BCrypt to compare passwords
		    if (!passwordEncoder.matches(request.getPassword(), staffDetails.getPassword())) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED,
		                "Invalid email or password"
		        );
		    }


		    Staff staff = staffRepository.findByEmail(normalizedEmail)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.UNAUTHORIZED,
		                    "Invalid email or password"
		            ));

		    if (!staff.isEmployeeActive() || !staff.isCanLogin()) {
		        throw new ResponseStatusException(
		                HttpStatus.FORBIDDEN,
		                "Staff is not allowed to login"
		        );
		    }

		    String roleUpperCase = staff.getRole() != null ? staff.getRole().toUpperCase() : "USER";
		    
		    System.out.println("Login - Staff ID: " + staff.getStaffId());
		    System.out.println("Login - Original Role: " + staff.getRole());
		    System.out.println("Login - Role for Token: " + roleUpperCase);
		    
		    String token = jwtService.generateToken(staff.getStaffId(), roleUpperCase);
		    
		    // Check if password reset is required
		    boolean requirePasswordReset = staffDetails.isRequirePasswordReset();

		    return new LoginResponse(token, 3600000L, staff.getRole(), staff.getStaffId(), requirePasswordReset);
		}
	

	@Override
	public List<StaffDetailsDto> getAllStaff() {		
		List<StaffDetailsDto> staffDetailsList = staffRepository.findByIsEmployeeActiveTrue().stream()
				.map(staff -> StaffDtoBuilder.buildStaffDetailsDto(staff))
				.toList();
		return staffDetailsList;
	}

	@Override
	public Staff getStaffByStaffId(String staffId) {
	    return staffRepository.findById(staffId)
							.orElseThrow(() -> new StaffNotFoundException("Staff with Id : "+ staffId +" not found"));
	}

	@Override
	public ResponseEntity<List<StaffDetailsDto>> searchByStaffFirstNameorLastName(String name) {
		List<Staff> staffs = staffRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name,name);
		
		List<StaffDetailsDto> staffDetailsDtoList = new ArrayList<>();
		
		if(staffs.isEmpty()) {
			throw new StaffNotFoundException("No Staff found with name : "+name);
		}
		
		for(Staff staff : staffs) {
			staffDetailsDtoList.add(StaffDtoBuilder.buildStaffDetailsDto(staff));
		}
		return ResponseEntity.ok(staffDetailsDtoList);
	}

	@Override
	public StaffDetailsDto updateStaff(String staffId, RegisterStaffDto registerStaffDto) {
		Staff existingStaff = staffRepository.findById(staffId)
						.orElseThrow(() -> new StaffNotFoundException("Staff with Id : "+ staffId + " not found"));
		
		if(!existingStaff.getEmail().equals(registerStaffDto.getEmail())) {
			if(staffRepository.findByEmail(registerStaffDto.getEmail()).isPresent()) {
				throw new IllegalArgumentException("Email "+registerStaffDto.getEmail() + " is already registered");
				
			}
		}
		
		if(!existingStaff.getPhoneNumber().equals(registerStaffDto.getPhoneNumber())) {
			if(staffRepository.findByPhoneNumber(registerStaffDto.getPhoneNumber()).isPresent()) {
				throw new IllegalArgumentException("Phone number " + registerStaffDto.getPhoneNumber() +" is already registered");
			}
		}
		
		Staff updatedStaff = StaffBuilder.updateStaffBuilder(registerStaffDto,existingStaff);
		
		Staff savedStaff = staffRepository.save(updatedStaff);		
		
		return StaffDtoBuilder.buildStaffDetailsDto(savedStaff);
	}

	@Override
	public void sendOtp(String email) {
		Optional<StaffDetails> optional = detailsRepository.findByEmail(email);
		
		if(optional.isEmpty()) {
			return;
		}
		
		StaffDetails staffDetails = optional.get();
		
		String otp = String.valueOf(new Random().nextInt(900000) + 100000);
		
		staffDetails.setResetOtp(otp);
		staffDetails.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
		
		detailsRepository.save(staffDetails);
		
		EmailRequestDto emailRequest = new EmailRequestDto();
		emailRequest.setTo(email);
		emailRequest.setSubject("Password Reset OTP - Medsync");
		emailRequest.setBody(
				"<h3>Your OTP for password reset is : <b>" + otp + "</b></h3>"
				+"<p>This OTP is valid for 5 minutes.</p>");
		notificationFeignClient.sendEmail(emailRequest);
		return;
	}

	@Override
	public void verifyOtp(VerifyOtpRequest otpRequest) {
		
		StaffDetails staff = detailsRepository.findByEmail(otpRequest.getEmail())
						.orElseThrow(() -> new InvalidOtpException("Invalid email or OTP"));
		
		if(staff.getResetOtp() == null ||
				!staff.getResetOtp().equals(otpRequest.getOtp()) ||
				staff.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
				
			throw new InvalidOtpException("Invalid or expired OTP");
		}
		
	}

	@Transactional
	@Override
	public void resetPassword(ResetPasswordRequest request) {

		StaffDetails staffDetails = detailsRepository.findByEmail(request.getEmail())
						.orElseThrow(() -> new ResponseStatusException(
								HttpStatus.BAD_REQUEST,
								"User not found"));
		
		String hashedPassword = passwordEncoder.encode(request.getNewPassword());
		staffDetails.setPassword(hashedPassword);
		staffDetails.setResetOtp(null);
		staffDetails.setOtpExpiryTime(null);
		staffDetails.setRequirePasswordReset(false);
		
		detailsRepository.save(staffDetails);
	}

	@Override
	public String deleteStaff(String staffId) {
		
		Staff staff = staffRepository.findById(staffId)
					.orElseThrow(() -> new StaffNotFoundException("No Staff with Id : "+ staffId));
		
		staff.setEmployeeActive(false);
		staff.setCanLogin(false);
		
		staffRepository.save(staff);
		return staff.getFirstName() + " " +staff.getLastName();
	}

	@Override
	public String getDoctorName(String doctorId) {
		
		Staff staff = staffRepository.findById(doctorId)
						.orElseThrow(() -> new DoctorNotFoundException("No doctor with ID : "+ doctorId));
		
		
		return staff.getFirstName() + " " + staff.getLastName();
	}

	
	
	
	
	

}
