
package com.flmhospitals.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.flmhospitals.dao.BedRepository;
import com.flmhospitals.dao.RoomRepository;
import com.flmhospitals.dto.BedDetailsResponseDTO;
import com.flmhospitals.dto.BedRequestDTO;
import com.flmhospitals.exception.BedNotFoundException;
import com.flmhospitals.exception.BedUnavailableException;
import com.flmhospitals.exception.RoomNotFoundException;
import com.flmhospitals.model.Bed;
import com.flmhospitals.model.Room;

@ExtendWith(MockitoExtension.class)
class BedServiceImplTest {

    @Mock
    private BedRepository bedRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BedServiceImpl bedService;

    private Room room;
    private Bed bed;
    private BedRequestDTO request;

    @BeforeEach
    void setUp() {

        room = Room.builder()
                .roomNumber(101)
                .roomType("General")
                .roomCapacity(3)
                .beds(new java.util.ArrayList<>())
                .build();

        bed = Bed.builder()
                .bedNumber(1001)
                .isOccupied(false)
                .patientId(0)
                .room(room)
                .build();

        room.getBeds().add(bed);

        request = new BedRequestDTO(
                1002,
                101,
                false
        );
    }

    // 1. Add bed successfully

    @Test
    void addBedInRoom_ShouldAddBed_WhenRoomExistsAndHasCapacity() {

        room.getBeds().clear();

        when(roomRepository.findByRoomNumber(101))
                .thenReturn(room);

        when(bedRepository.save(any(Bed.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response =
                bedService.addBedInRoom(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Successfully added"));

        verify(bedRepository).save(any(Bed.class));
    }

    // 2. Room does not exist

    @Test
    void addBedInRoom_ShouldThrowException_WhenRoomDoesNotExist() {

        when(roomRepository.findByRoomNumber(101))
                .thenReturn(null);

        assertThrows(
                RoomNotFoundException.class,
                () -> bedService.addBedInRoom(request)
        );

        verify(bedRepository, never()).save(any(Bed.class));
    }

    // 3. Duplicate bed number

    @Test
    void addBedInRoom_ShouldThrowException_WhenBedAlreadyExists() {

        Bed existingBed = Bed.builder()
                .bedNumber(1002)
                .room(room)
                .build();

        room.setBeds(List.of(existingBed));

        when(roomRepository.findByRoomNumber(101))
                .thenReturn(room);

        assertThrows(
                IllegalArgumentException.class,
                () -> bedService.addBedInRoom(request)
        );

        verify(bedRepository, never()).save(any(Bed.class));
    }

    // 4. Room capacity is full

    @Test
    void addBedInRoom_ShouldThrowException_WhenRoomIsFull() {

        room.setBeds(List.of(
                Bed.builder().bedNumber(1001).build(),
                Bed.builder().bedNumber(1002).build(),
                Bed.builder().bedNumber(1003).build()
        ));

        request.setBedNumber(1004);

        when(roomRepository.findByRoomNumber(101))
                .thenReturn(room);

        assertThrows(
                IllegalStateException.class,
                () -> bedService.addBedInRoom(request)
        );

        verify(bedRepository, never()).save(any(Bed.class));
    }

    // 5. Remove bed successfully

    @Test
    void removeBed_ShouldDeleteBed_WhenBedExists() {

        when(bedRepository.findBedWithRoomNumber(1001, 101))
                .thenReturn(Optional.of(bed));

        ResponseEntity<String> response =
                bedService.removeBed(1001, 101);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bed removed from the room", response.getBody());

        verify(bedRepository).delete(bed);
    }

    // 6. Remove bed when it does not exist

    @Test
    void removeBed_ShouldThrowException_WhenBedDoesNotExist() {

        when(bedRepository.findBedWithRoomNumber(9999, 101))
                .thenReturn(Optional.empty());

        assertThrows(
                BedNotFoundException.class,
                () -> bedService.removeBed(9999, 101)
        );

        verify(bedRepository, never()).delete(any(Bed.class));
    }

    // 7. Get all bed details

    @Test
    void getAllBedDetails_ShouldReturnAllBeds() {

        when(bedRepository.findAll())
                .thenReturn(List.of(bed));

        List<BedDetailsResponseDTO> response =
                bedService.getAllBedDetails();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1001, response.get(0).getBedNumber());
        assertEquals(101, response.get(0).getRoomNumber());

        verify(bedRepository).findAll();
    }

    // 8. Get beds by room number

    @Test
    void getBedsByRoomId_ShouldReturnBeds_WhenBedsExist() {

        when(roomRepository.findById(101L))
                .thenReturn(Optional.of(room));

        when(bedRepository.findByRoomRoomNumber(101))
                .thenReturn(List.of(bed));

        List<BedDetailsResponseDTO> response =
                bedService.getBedsByRoomId(101);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1001, response.get(0).getBedNumber());

        verify(bedRepository).findByRoomRoomNumber(101);
    }

    // 9. No beds in room

    @Test
    void getBedsByRoomId_ShouldThrowException_WhenNoBedsExist() {

        when(roomRepository.findById(101L))
                .thenReturn(Optional.of(room));

        when(bedRepository.findByRoomRoomNumber(101))
                .thenReturn(List.of());

        assertThrows(
                BedNotFoundException.class,
                () -> bedService.getBedsByRoomId(101)
        );
    }

    // 10. Get vacant beds

    @Test
    void getVacantBedsByRoomNumber_ShouldReturnVacantBeds() {

        when(roomRepository.findById(101L))
                .thenReturn(Optional.of(room));

        when(bedRepository.findByRoomRoomNumberAndIsOccupiedFalse(101))
                .thenReturn(List.of(bed));

        List<BedDetailsResponseDTO> response =
                bedService.getVacantBedsByRoomNumber(101);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertFalse(response.get(0).isOccupied());

        verify(bedRepository)
                .findByRoomRoomNumberAndIsOccupiedFalse(101);
    }

    // 11. No vacant beds

    @Test
    void getVacantBedsByRoomNumber_ShouldThrowException_WhenNoVacantBedsExist() {

        when(roomRepository.findById(101L))
                .thenReturn(Optional.of(room));

        when(bedRepository.findByRoomRoomNumberAndIsOccupiedFalse(101))
                .thenReturn(List.of());

        assertThrows(
                BedUnavailableException.class,
                () -> bedService.getVacantBedsByRoomNumber(101)
        );
    }

    // 12. Update bed successfully

    @Test
    void updateBedDetails_ShouldUpdateBed_WhenBedExists() {

        BedRequestDTO updateRequest =
                new BedRequestDTO(1001, 101, true);

        when(roomRepository.findById(101L))
                .thenReturn(Optional.of(room));

        when(bedRepository.findBedWithRoomNumber(1001, 101))
                .thenReturn(Optional.of(bed));

        when(bedRepository.save(any(Bed.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<BedDetailsResponseDTO> response =
                bedService.updateBedDetails(101, 1001, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isOccupied());

        verify(bedRepository).save(bed);
    }
}
