package com.oot.clinic.services;

import com.oot.clinic.DTOs.office.OfficeResponseDTO;
import com.oot.clinic.entities.Office;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.OfficeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceTests {

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private OfficeService officeService;

    @Test
    void addOfficeShouldReturnOfficeResponseDTO() {
        // Given
        when(officeRepository.findByRoomNumber(anyInt())).thenReturn(Optional.empty());
        when(officeRepository.save(any(Office.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OfficeResponseDTO result = officeService.addOffice(101);

        // Then
        assertNotNull(result);
        assertEquals(101, result.getRoomNumber());
        verify(officeRepository, times(1)).findByRoomNumber(101);
        verify(officeRepository, times(1)).save(any(Office.class));
    }

    @Test
    void addOfficeShouldThrowValidationExceptionWhenRoomNumberIsZero() {
        // When & Then
        assertThrows(ValidationException.class, () -> officeService.addOffice(0));
        verify(officeRepository, never()).save(any(Office.class));
    }

    @Test
    void addOfficeShouldThrowValidationExceptionWhenRoomNumberIsNegative() {
        // When & Then
        assertThrows(ValidationException.class, () -> officeService.addOffice(-1));
        verify(officeRepository, never()).save(any(Office.class));
    }

    @Test
    void addOfficeShouldThrowConflictExceptionWhenRoomNumberExists() {
        // Given
        Office existingOffice = new Office(101);
        when(officeRepository.findByRoomNumber(101)).thenReturn(Optional.of(existingOffice));

        // When & Then
        assertThrows(ConflictException.class, () -> officeService.addOffice(101));
        verify(officeRepository, times(1)).findByRoomNumber(101);
        verify(officeRepository, never()).save(any(Office.class));
    }

    @Test
    void getOfficesShouldReturnListOfOfficeResponseDTO() {
        // Given
        Office office1 = new Office(101);
        Office office2 = new Office(102);
        when(officeRepository.findAll()).thenReturn(List.of(office1, office2));

        // When
        List<OfficeResponseDTO> result = officeService.getOffices();

        // Then
        assertEquals(2, result.size());
        verify(officeRepository, times(1)).findAll();
    }

    @Test
    void deleteOfficeByIdShouldDeleteOffice() {
        // Given
        Office office = new Office(101);
        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));
        doNothing().when(officeRepository).delete(any(Office.class));

        // When
        officeService.deleteOfficeById(1L);

        // Then
        verify(officeRepository, times(1)).findById(1L);
        verify(officeRepository, times(1)).delete(office);
    }

    @Test
    void deleteOfficeByIdShouldThrowResourceNotFoundExceptionWhenNotFound() {
        // Given
        when(officeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> officeService.deleteOfficeById(999L));
        verify(officeRepository, times(1)).findById(999L);
        verify(officeRepository, never()).delete(any(Office.class));
    }

    @Test
    void deleteOfficeByIdShouldThrowConflictExceptionWhenOfficeHasShifts() {
        // Given
        Office office = new Office(101);
        office.getShifts().add(new com.oot.clinic.entities.Shift());
        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));

        // When & Then
        assertThrows(ConflictException.class, () -> officeService.deleteOfficeById(1L));
        verify(officeRepository, times(1)).findById(1L);
        verify(officeRepository, never()).delete(any(Office.class));
    }
}

