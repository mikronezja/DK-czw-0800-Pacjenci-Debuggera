package com.oot.clinic.services;

import com.oot.clinic.DTOs.shift.ShiftResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.OfficeRepository;
import com.oot.clinic.repositories.ShiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTests {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private ShiftService shiftService;

    private void setId(Shift shift, Long id) {
        try {
            Field idField = Shift.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(shift, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllShiftsShouldReturnListOfShiftResponseDTO() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        Office office = new Office(101);
        Shift shift = new Shift(doctor, office, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        setId(shift, 1L);
        when(shiftRepository.findAll()).thenReturn(List.of(shift));

        // When
        List<ShiftResponseDTO> result = shiftService.getAllShifts();

        // Then
        assertEquals(1, result.size());
        verify(shiftRepository, times(1)).findAll();
    }

    @Test
    void createShiftShouldReturnShiftResponseDTO() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        Office office = new Office(101);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));
        when(shiftRepository.findAll()).thenReturn(List.of());
        when(shiftRepository.save(any(Shift.class))).thenAnswer(invocation -> {
            Shift savedShift = invocation.getArgument(0);
            setId(savedShift, 1L);
            return savedShift;
        });

        // When
        ShiftResponseDTO result = shiftService.createShift(1L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

        // Then
        assertNotNull(result);
        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        verify(doctorRepository, times(1)).findById(1L);
        verify(officeRepository, times(1)).findById(1L);
        verify(shiftRepository, times(1)).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowValidationExceptionWhenDoctorIdIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                shiftService.createShift(null, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowValidationExceptionWhenOfficeIdIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                shiftService.createShift(1L, null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowValidationExceptionWhenDayOfWeekIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                shiftService.createShift(1L, 1L, null, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowValidationExceptionWhenStartTimeIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                shiftService.createShift(1L, 1L, DayOfWeek.MONDAY, null, LocalTime.of(17, 0)));
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowValidationExceptionWhenEndTimeIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                shiftService.createShift(1L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), null));
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowValidationExceptionWhenStartTimeIsAfterEndTime() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                shiftService.createShift(1L, 1L, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(9, 0)));
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowResourceNotFoundExceptionWhenDoctorNotFound() {
        // Given
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                shiftService.createShift(999L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        verify(doctorRepository, times(1)).findById(999L);
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void createShiftShouldThrowResourceNotFoundExceptionWhenOfficeNotFound() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                shiftService.createShift(1L, 999L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        verify(doctorRepository, times(1)).findById(1L);
        verify(officeRepository, times(1)).findById(999L);
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    void deleteShiftByIdShouldDeleteShift() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        Office office = new Office(101);
        Shift shift = new Shift(doctor, office, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        doNothing().when(shiftRepository).delete(any(Shift.class));

        // When
        shiftService.deleteShiftById(1L);

        // Then
        verify(shiftRepository, times(1)).findById(1L);
        verify(shiftRepository, times(1)).delete(shift);
    }

    @Test
    void deleteShiftByIdShouldThrowResourceNotFoundExceptionWhenNotFound() {
        // Given
        when(shiftRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> shiftService.deleteShiftById(999L));
        verify(shiftRepository, times(1)).findById(999L);
        verify(shiftRepository, never()).delete(any(Shift.class));
    }
}

