
package com.flmhospitals.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flmhospitals.dao.PatientRepository;
import com.flmhospitals.dto.PatientAddressrequestDto;
import com.flmhospitals.dto.RegisterPatientRequestDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;
import com.flmhospitals.exception.PatientNotFoundException;
import com.flmhospitals.model.Patient;
import com.flmhospitals.model.PatientAddress;
import com.flmhospitals.utils.Gender;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private RegisterPatientRequestDto request;

    private Patient patient;

    @BeforeEach
    void setUp() {

        PatientAddressrequestDto addressRequest =
                new PatientAddressrequestDto(
                        "H.No 123",
                        "Near Bus Stand",
                        "Hyderabad",
                        "Telangana",
                        "India",
                        "500001"
                );

        request = new RegisterPatientRequestDto(
                "Lokesh",
                "9876543210",
                Gender.MALE,
                "lokesh@gmail.com",
                LocalDate.of(2002, 5, 15),
                addressRequest
        );

        PatientAddress address = PatientAddress.builder()
                .patientAddressId(101L)
                .doorNumber("H.No 123")
                .landmark("Near Bus Stand")
                .city("Hyderabad")
                .state("Telangana")
                .country("India")
                .pinCode("500001")
                .build();

        patient = Patient.builder()
                .patientId("P101")
                .patientName("Lokesh")
                .gender(Gender.MALE)
                .patientEmail("lokesh@gmail.com")
                .patientPhoneNumber("9876543210")
                .dateOfBirth(LocalDate.of(2002, 5, 15))
                .patientAddress(address)
                .build();
    }

    // 1. Successfully register patient

    @Test
    void registerPatient_ShouldReturnPatientDetails_WhenRegistrationIsSuccessful() {

        when(patientRepository.existsByPatientEmail(anyString()))
                .thenReturn(false);

        when(patientRepository.existsByPatientPhoneNumber(anyString()))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(patient);

        RegisterPatientResponseDto response =
                patientService.registerPatient(request);

        assertNotNull(response);
        assertEquals("P101", response.getPatientId());
        assertEquals("Lokesh", response.getPatientName());
        assertEquals("lokesh@gmail.com", response.getPatientEmail());

        verify(patientRepository).save(any(Patient.class));
    }

    // 2. Duplicate email

    @Test
    void registerPatient_ShouldThrowException_WhenEmailAlreadyExists() {

        when(patientRepository.existsByPatientEmail(anyString()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.registerPatient(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));

        verify(patientRepository, never()).save(any(Patient.class));
        verify(patientRepository, never())
                .existsByPatientPhoneNumber(anyString());
    }

    // 3. Duplicate phone number

    @Test
    void registerPatient_ShouldThrowException_WhenPhoneNumberAlreadyExists() {

        when(patientRepository.existsByPatientEmail(anyString()))
                .thenReturn(false);

        when(patientRepository.existsByPatientPhoneNumber(anyString()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.registerPatient(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));

        verify(patientRepository, never()).save(any(Patient.class));
    }

    // 4. Get all patients

    @Test
    void getAllPatients_ShouldReturnAllPatients() {

        when(patientRepository.findAll())
                .thenReturn(List.of(patient));

        List<RegisterPatientResponseDto> response =
                patientService.getAllPatients();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Lokesh", response.get(0).getPatientName());

        verify(patientRepository).findAll();
    }

    // 5. Get all patients when database is empty

    @Test
    void getAllPatients_ShouldReturnEmptyList_WhenNoPatientsExist() {

        when(patientRepository.findAll())
                .thenReturn(List.of());

        List<RegisterPatientResponseDto> response =
                patientService.getAllPatients();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(patientRepository).findAll();
    }

    // 6. Get patient by ID

    @Test
    void getPatientById_ShouldReturnPatient_WhenPatientExists() {

        when(patientRepository.findById("P101"))
                .thenReturn(Optional.of(patient));

        RegisterPatientResponseDto response =
                patientService.getPatientById("P101");

        assertNotNull(response);
        assertEquals("P101", response.getPatientId());
        assertEquals("Lokesh", response.getPatientName());

        verify(patientRepository).findById("P101");
    }

    // 7. Patient not found by ID

    @Test
    void getPatientById_ShouldThrowException_WhenPatientDoesNotExist() {

        when(patientRepository.findById("P999"))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatientById("P999")
        );

        verify(patientRepository).findById("P999");
    }

    // 8. Successfully update patient

    @Test
    void updatePatient_ShouldUpdatePatient_WhenPatientExists() {

        when(patientRepository.findById("P101"))
                .thenReturn(Optional.of(patient));

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegisterPatientResponseDto response =
                patientService.updatePatient(request, "P101");

        assertNotNull(response);
        assertEquals("P101", response.getPatientId());
        assertEquals("Lokesh", response.getPatientName());

        // Verify that the existing address ID is preserved
        assertEquals(
                101L,
                response.getPatientAddress().getPatientAddressId()
        );

        verify(patientRepository).save(any(Patient.class));
    }

    // 9. Update patient when patient does not exist

    @Test
    void updatePatient_ShouldThrowException_WhenPatientDoesNotExist() {

        when(patientRepository.findById("P999"))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.updatePatient(request, "P999")
        );

        verify(patientRepository, never()).save(any(Patient.class));
    }

    // 10. Get patient name

    @Test
    void getPatientName_ShouldReturnName_WhenPatientExists() {

        when(patientRepository.findById("P101"))
                .thenReturn(Optional.of(patient));

        String patientName = patientService.getPatientName("P101");

        assertEquals("Lokesh", patientName);

        verify(patientRepository).findById("P101");
    }

    // 11. Get patient name when patient does not exist

    @Test
    void getPatientName_ShouldThrowException_WhenPatientDoesNotExist() {

        when(patientRepository.findById("P999"))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatientName("P999")
        );

        verify(patientRepository).findById("P999");
    }
}
