package com.flmhospitals.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.flmhospitals.client.NotificationFeignClient;
import com.flmhospitals.dao.StaffDetailsRepository;
import com.flmhospitals.dao.StaffRepository;
import com.flmhospitals.dto.EmailRequestDto;
import com.flmhospitals.dto.LoginRequest;
import com.flmhospitals.dto.LoginResponse;
import com.flmhospitals.dto.RegisterStaffDto;
import com.flmhospitals.dto.ResetPasswordRequest;
import com.flmhospitals.dto.StaffAddressDto;
import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.dto.VerifyOtpRequest;
import com.flmhospitals.enums.Specialization;
import com.flmhospitals.enums.StaffType;
import com.flmhospitals.exception.DoctorNotFoundException;
import com.flmhospitals.exception.GlobalExceptionHandler;
import com.flmhospitals.exception.InvalidOtpException;
import com.flmhospitals.exception.StaffNotFoundException;
import com.flmhospitals.model.Staff;
import com.flmhospitals.model.StaffAddress;
import com.flmhospitals.model.StaffDetails;
import com.flmhospitals.security.JwtService;
import com.flmhospitals.service.Impl.StaffServiceImpl;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StaffDetailsRepository detailsRepository;

    @Mock
    private NotificationFeignClient notificationFeignClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private StaffServiceImpl staffService;

    private Staff staff;
    private StaffDetails staffDetails;
    private RegisterStaffDto registerRequest;
    private StaffAddress staffAddress;

    @BeforeEach
    void setUp() {

        StaffAddressDto addressDto = new StaffAddressDto(
                "Near Bus Stand",
                "Hyderabad",
                "Telangana",
                "India",
                "500001"
        );

        registerRequest = new RegisterStaffDto(
                "Lokesh",
                "Perumandla",
                "9876543210",
                "ADMIN",
                "Male",
                LocalDate.of(2025, 1, 10),
                2,
                "lokesh@gmail.com",
                addressDto,
                Specialization.GENERAL_PHYSICIAN,
                StaffType.DOCTOR
        );

        staffAddress = StaffAddress.builder()
                .staffAddressId(101L)
                .landmark("Near Bus Stand")
                .city("Hyderabad")
                .state("Telangana")
                .country("India")
                .pincode("500001")
                .build();

        staffDetails = StaffDetails.builder()
                .staffDetailsId(1L)
                .email("lokesh@gmail.com")
                .password("encoded-password")
                .requirePasswordReset(true)
                .build();

        staff = Staff.builder()
                .staffId("FLM-101")
                .firstName("Lokesh")
                .lastName("Perumandla")
                .phoneNumber("9876543210")
                .gender("Male")
                .role("ADMIN")
                .dateOfJoining(LocalDate.of(2025, 1, 10))
                .experienceInYears(2)
                .email("lokesh@gmail.com")
                .canLogin(true)
                .isEmployeeActive(true)
                .staffType(StaffType.DOCTOR)
                .specialization(Specialization.GENERAL_PHYSICIAN)
                .staffAddress(staffAddress)
                .staffDetails(staffDetails)
                .build();
    }

    // =====================================================
    // 1. STAFF REGISTRATION - 3 TEST CASES
    // =====================================================

    @Test
    void registerStaffDetails_ShouldRegisterStaffSuccessfully() {

        when(staffRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(staffRepository.findByPhoneNumber(registerRequest.getPhoneNumber()))
                .thenReturn(Optional.empty());

        when(staffRepository.save(any(Staff.class)))
                .thenAnswer(invocation -> {
                    Staff saved = invocation.getArgument(0);
                    saved.setStaffId("FLM-101");
                    return saved;
                });

        when(notificationFeignClient.sendEmail(any(EmailRequestDto.class)))
                .thenReturn("Email sent successfully");

        StaffDetailsDto response =
                staffService.registerStaffDetails(registerRequest);

        assertNotNull(response);
        assertEquals("FLM-101", response.getStaffId());
        assertEquals("Lokesh", response.getFirstName());
        assertEquals("lokesh@gmail.com", response.getEmail());

        verify(staffRepository).save(any(Staff.class));
        verify(notificationFeignClient).sendEmail(any(EmailRequestDto.class));
    }

    @Test
    void registerStaffDetails_ShouldThrowException_WhenEmailAlreadyExists() {

        when(staffRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.of(staff));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> staffService.registerStaffDetails(registerRequest)
        );

        assertTrue(exception.getMessage().contains("already registered"));

        verify(staffRepository, never()).save(any(Staff.class));
        verify(notificationFeignClient, never())
                .sendEmail(any(EmailRequestDto.class));
    }

    @Test
    void registerStaffDetails_ShouldThrowException_WhenPhoneAlreadyExists() {

        when(staffRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(staffRepository.findByPhoneNumber(registerRequest.getPhoneNumber()))
                .thenReturn(Optional.of(staff));

        assertThrows(
                IllegalArgumentException.class,
                () -> staffService.registerStaffDetails(registerRequest)
        );

        verify(staffRepository, never()).save(any(Staff.class));
    }

    // =====================================================
    // 2. LOGIN - 5 TEST CASES
    // =====================================================

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {

        LoginRequest request =
                new LoginRequest("  LOKESH@GMAIL.COM  ", "password123");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        when(passwordEncoder.matches("password123", "encoded-password"))
                .thenReturn(true);

        when(staffRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staff));

        when(jwtService.generateToken("FLM-101", "ADMIN"))
                .thenReturn("mock-jwt-token");

        LoginResponse response = staffService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("FLM-101", response.getStaffId());
        assertEquals("ADMIN", response.getRole());
        assertTrue(response.isRequirePasswordReset());

        verify(jwtService).generateToken("FLM-101", "ADMIN");
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenEmailDoesNotExist() {

        LoginRequest request =
                new LoginRequest("unknown@gmail.com", "password123");

        when(detailsRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenPasswordIsIncorrect() {

        LoginRequest request =
                new LoginRequest("lokesh@gmail.com", "wrong-password");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(staffRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenStaffRecordDoesNotExist() {

        LoginRequest request =
                new LoginRequest("lokesh@gmail.com", "password123");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        when(passwordEncoder.matches("password123", "encoded-password"))
                .thenReturn(true);

        when(staffRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_ShouldThrowForbidden_WhenStaffIsInactive() {

        staff.setEmployeeActive(false);

        LoginRequest request =
                new LoginRequest("lokesh@gmail.com", "password123");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        when(passwordEncoder.matches("password123", "encoded-password"))
                .thenReturn(true);

        when(staffRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staff));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffService.login(request)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    // =====================================================
    // 3. GET ALL STAFF - 2 TEST CASES
    // =====================================================

    @Test
    void getAllStaff_ShouldReturnAllActiveStaff() {

        when(staffRepository.findByIsEmployeeActiveTrue())
                .thenReturn(List.of(staff));

        List<StaffDetailsDto> response = staffService.getAllStaff();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Lokesh", response.get(0).getFirstName());

        verify(staffRepository).findByIsEmployeeActiveTrue();
    }

    @Test
    void getAllStaff_ShouldReturnEmptyList_WhenNoActiveStaffExists() {

        when(staffRepository.findByIsEmployeeActiveTrue())
                .thenReturn(List.of());

        List<StaffDetailsDto> response = staffService.getAllStaff();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    // =====================================================
    // 4. GET STAFF BY ID - 2 TEST CASES
    // =====================================================

    @Test
    void getStaffByStaffId_ShouldReturnStaff_WhenStaffExists() {

        when(staffRepository.findById("FLM-101"))
                .thenReturn(Optional.of(staff));

        Staff response = staffService.getStaffByStaffId("FLM-101");

        assertNotNull(response);
        assertEquals("FLM-101", response.getStaffId());
        assertEquals("Lokesh", response.getFirstName());

        verify(staffRepository).findById("FLM-101");
    }

    @Test
    void getStaffByStaffId_ShouldThrowException_WhenStaffDoesNotExist() {

        when(staffRepository.findById("FLM-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                StaffNotFoundException.class,
                () -> staffService.getStaffByStaffId("FLM-999")
        );
    }

    // =====================================================
    // 5. SEARCH STAFF - 2 TEST CASES
    // =====================================================

    @Test
    void searchByStaffFirstNameorLastName_ShouldReturnMatchingStaff() {

        when(staffRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        "Lokesh", "Lokesh"))
                .thenReturn(List.of(staff));

        ResponseEntity<List<StaffDetailsDto>> response =
                staffService.searchByStaffFirstNameorLastName("Lokesh");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Lokesh", response.getBody().get(0).getFirstName());
    }

    @Test
    void searchByStaffFirstNameorLastName_ShouldThrowException_WhenNoMatchExists() {

        when(staffRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        "Unknown", "Unknown"))
                .thenReturn(List.of());

        assertThrows(
                StaffNotFoundException.class,
                () -> staffService.searchByStaffFirstNameorLastName("Unknown")
        );
    }

    // =====================================================
    // 6. UPDATE STAFF - 4 TEST CASES
    // =====================================================

    @Test
    void updateStaff_ShouldUpdateStaffSuccessfully() {

        when(staffRepository.findById("FLM-101"))
                .thenReturn(Optional.of(staff));

        when(staffRepository.save(any(Staff.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StaffDetailsDto response =
                staffService.updateStaff("FLM-101", registerRequest);

        assertNotNull(response);
        assertEquals("Lokesh", response.getFirstName());
        assertEquals("lokesh@gmail.com", response.getEmail());

        verify(staffRepository).save(staff);
    }

    @Test
    void updateStaff_ShouldThrowException_WhenStaffDoesNotExist() {

        when(staffRepository.findById("FLM-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                StaffNotFoundException.class,
                () -> staffService.updateStaff("FLM-999", registerRequest)
        );

        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void updateStaff_ShouldThrowException_WhenNewEmailAlreadyExists() {

        RegisterStaffDto updateRequest = new RegisterStaffDto(
                "Lokesh", "Perumandla", "9876543210",
                "ADMIN", "Male", LocalDate.of(2025, 1, 10),
                2, "other@gmail.com",
                registerRequest.getStaffAddrssDto(),
                Specialization.GENERAL_PHYSICIAN,
                StaffType.DOCTOR
        );

        when(staffRepository.findById("FLM-101"))
                .thenReturn(Optional.of(staff));

        when(staffRepository.findByEmail("other@gmail.com"))
                .thenReturn(Optional.of(new Staff()));

        assertThrows(
                IllegalArgumentException.class,
                () -> staffService.updateStaff("FLM-101", updateRequest)
        );

        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void updateStaff_ShouldThrowException_WhenNewPhoneAlreadyExists() {

        RegisterStaffDto updateRequest = new RegisterStaffDto(
                "Lokesh", "Perumandla", "9123456780",
                "ADMIN", "Male", LocalDate.of(2025, 1, 10),
                2, "lokesh@gmail.com",
                registerRequest.getStaffAddrssDto(),
                Specialization.GENERAL_PHYSICIAN,
                StaffType.DOCTOR
        );

        when(staffRepository.findById("FLM-101"))
                .thenReturn(Optional.of(staff));

        when(staffRepository.findByPhoneNumber("9123456780"))
                .thenReturn(Optional.of(new Staff()));

        assertThrows(
                IllegalArgumentException.class,
                () -> staffService.updateStaff("FLM-101", updateRequest)
        );

        verify(staffRepository, never()).save(any(Staff.class));
    }

    // =====================================================
    // 7. SEND OTP - 2 TEST CASES
    // =====================================================

    @Test
    void sendOtp_ShouldGenerateAndSendOtp_WhenEmailExists() {

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        ArgumentCaptor<EmailRequestDto> emailCaptor =
                ArgumentCaptor.forClass(EmailRequestDto.class);

        staffService.sendOtp("lokesh@gmail.com");

        assertNotNull(staffDetails.getResetOtp());
        assertTrue(staffDetails.getResetOtp().matches("\\d{6}"));
        assertNotNull(staffDetails.getOtpExpiryTime());

        assertTrue(staffDetails.getOtpExpiryTime()
                .isAfter(LocalDateTime.now()));

        verify(detailsRepository).save(staffDetails);

        verify(notificationFeignClient).sendEmail(emailCaptor.capture());

        assertEquals(
                "lokesh@gmail.com",
                emailCaptor.getValue().getTo()
        );
    }

    @Test
    void sendOtp_ShouldNotSendEmail_WhenEmailDoesNotExist() {

        when(detailsRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        staffService.sendOtp("unknown@gmail.com");

        verify(detailsRepository, never()).save(any(StaffDetails.class));

        verify(notificationFeignClient, never())
                .sendEmail(any(EmailRequestDto.class));
    }

    // =====================================================
    // 8. VERIFY OTP - 4 TEST CASES
    // =====================================================

    @Test
    void verifyOtp_ShouldCompleteSuccessfully_WhenOtpIsValid() {

        staffDetails.setResetOtp("123456");
        staffDetails.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        VerifyOtpRequest request =
                new VerifyOtpRequest("lokesh@gmail.com", "123456");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        assertDoesNotThrow(() -> staffService.verifyOtp(request));

        verify(detailsRepository).findByEmail("lokesh@gmail.com");
    }

    @Test
    void verifyOtp_ShouldThrowException_WhenEmailDoesNotExist() {

        VerifyOtpRequest request =
                new VerifyOtpRequest("unknown@gmail.com", "123456");

        when(detailsRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidOtpException.class,
                () -> staffService.verifyOtp(request)
        );
    }

    @Test
    void verifyOtp_ShouldThrowException_WhenOtpIsIncorrect() {

        staffDetails.setResetOtp("123456");
        staffDetails.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        VerifyOtpRequest request =
                new VerifyOtpRequest("lokesh@gmail.com", "999999");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        assertThrows(
                InvalidOtpException.class,
                () -> staffService.verifyOtp(request)
        );
    }

    @Test
    void verifyOtp_ShouldThrowException_WhenOtpIsExpired() {

        staffDetails.setResetOtp("123456");
        staffDetails.setOtpExpiryTime(LocalDateTime.now().minusMinutes(5));

        VerifyOtpRequest request =
                new VerifyOtpRequest("lokesh@gmail.com", "123456");

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        assertThrows(
                InvalidOtpException.class,
                () -> staffService.verifyOtp(request)
        );
    }

    // =====================================================
    // 9. RESET PASSWORD - 2 TEST CASES
    // =====================================================

    @Test
    void resetPassword_ShouldUpdatePasswordAndClearOtp() {

        staffDetails.setResetOtp("123456");
        staffDetails.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "lokesh@gmail.com",
                        "newPassword123"
                );

        when(detailsRepository.findByEmail("lokesh@gmail.com"))
                .thenReturn(Optional.of(staffDetails));

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("new-encoded-password");

        staffService.resetPassword(request);

        assertEquals(
                "new-encoded-password",
                staffDetails.getPassword()
        );

        assertNull(staffDetails.getResetOtp());
        assertNull(staffDetails.getOtpExpiryTime());
        assertFalse(staffDetails.isRequirePasswordReset());

        verify(detailsRepository).save(staffDetails);
    }

    @Test
    void resetPassword_ShouldThrowException_WhenEmailDoesNotExist() {

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "unknown@gmail.com",
                        "newPassword123"
                );

        when(detailsRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffService.resetPassword(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(passwordEncoder, never()).encode(anyString());
        verify(detailsRepository, never()).save(any(StaffDetails.class));
    }

    // =====================================================
    // 10. DELETE STAFF - 2 TEST CASES
    // =====================================================

    @Test
    void deleteStaff_ShouldDeactivateStaff_WhenStaffExists() {

        when(staffRepository.findById("FLM-101"))
                .thenReturn(Optional.of(staff));

        when(staffRepository.save(any(Staff.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String response = staffService.deleteStaff("FLM-101");

        assertEquals("Lokesh Perumandla", response);
        assertFalse(staff.isEmployeeActive());
        assertFalse(staff.isCanLogin());

        verify(staffRepository).save(staff);
    }

    @Test
    void deleteStaff_ShouldThrowException_WhenStaffDoesNotExist() {

        when(staffRepository.findById("FLM-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                StaffNotFoundException.class,
                () -> staffService.deleteStaff("FLM-999")
        );

        verify(staffRepository, never()).save(any(Staff.class));
    }

    // =====================================================
    // 11. GET DOCTOR NAME - 2 TEST CASES
    // =====================================================

    @Test
    void getDoctorName_ShouldReturnDoctorName_WhenDoctorExists() {

        when(staffRepository.findById("FLM-101"))
                .thenReturn(Optional.of(staff));

        String response = staffService.getDoctorName("FLM-101");

        assertEquals("Lokesh Perumandla", response);

        verify(staffRepository).findById("FLM-101");
    }

    @Test
    void getDoctorName_ShouldThrowException_WhenDoctorDoesNotExist() {

        when(staffRepository.findById("FLM-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> staffService.getDoctorName("FLM-999")
        );
    }
}

