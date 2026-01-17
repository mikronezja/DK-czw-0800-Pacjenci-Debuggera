package com.oot.clinic.services;

import com.oot.clinic.DTOs.doctor.DoctorDTO;
import com.oot.clinic.DTOs.doctor.DoctorResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTests {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void addDoctorShouldReturnDoctorResponseDTO() {
        // Given
        when(doctorRepository.findByPesel(anyString())).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorResponseDTO result = doctorService.addDoctor("Jan", "Kowalski", "80010112345", "Warszawa", Specialization.KARDIOLOG);

        // Then
        assertNotNull(result);
        assertEquals("Jan", result.getName());
        assertEquals("Kowalski", result.getSurname());
        assertEquals(Specialization.KARDIOLOG, result.getSpecialization());
        verify(doctorRepository, times(1)).findByPesel("80010112345");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldThrowValidationExceptionWhenNameIsEmpty() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                doctorService.addDoctor("", "Kowalski", "80010112345", "Warszawa", Specialization.KARDIOLOG));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldThrowValidationExceptionWhenSurnameIsEmpty() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                doctorService.addDoctor("Jan", "", "80010112345", "Warszawa", Specialization.KARDIOLOG));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldThrowValidationExceptionWhenSpecializationIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                doctorService.addDoctor("Jan", "Kowalski", "80010112345", "Warszawa", null));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldThrowValidationExceptionWhenNameIsTooLong() {
        // Given
        String longName = "x".repeat(101);

        // When & Then
        assertThrows(ValidationException.class, () ->
                doctorService.addDoctor(longName, "Kowalski", "80010112345", "Warszawa", Specialization.KARDIOLOG));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldThrowValidationExceptionWhenPeselIsInvalid() {
        // When & Then
        assertThrows(ValidationException.class, () ->
                doctorService.addDoctor("Jan", "Kowalski", "12345", "Warszawa", Specialization.KARDIOLOG));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldThrowConflictExceptionWhenPeselExists() {
        // Given
        Doctor existingDoctor = new Doctor("Existing", "Doctor", "80010112345", Specialization.KARDIOLOG, "Address");
        when(doctorRepository.findByPesel("80010112345")).thenReturn(Optional.of(existingDoctor));

        // When & Then
        assertThrows(ConflictException.class, () ->
                doctorService.addDoctor("Jan", "Kowalski", "80010112345", "Warszawa", Specialization.KARDIOLOG));
        verify(doctorRepository, times(1)).findByPesel("80010112345");
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctorShouldTrimWhitespaces() {
        // Given
        when(doctorRepository.findByPesel(anyString())).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorResponseDTO result = doctorService.addDoctor("  Jan  ", "  Kowalski  ", "80010112345", "  Warszawa  ", Specialization.KARDIOLOG);

        // Then
        assertEquals("Jan", result.getName());
        assertEquals("Kowalski", result.getSurname());
        assertEquals("Warszawa", result.getAddress());
    }

    @Test
    void getDoctorsShouldReturnListOfDoctorDTO() {
        // Given
        Doctor doctor1 = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        Doctor doctor2 = new Doctor("Anna", "Nowak", "82030554321", Specialization.DERMATOLOG, "Kraków");
        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        // When
        List<DoctorDTO> result = doctorService.getDoctors();

        // Then
        assertEquals(2, result.size());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void getDoctorByIdShouldReturnDoctor() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // When
        Doctor result = doctorService.getDoctorById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Jan", result.getName());
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void getDoctorByIdShouldThrowResourceNotFoundExceptionWhenNotFound() {
        // Given
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(999L));
        verify(doctorRepository, times(1)).findById(999L);
    }

    @Test
    void deleteDoctorByIdShouldDeleteDoctor() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).delete(any(Doctor.class));

        // When
        doctorService.deleteDoctorById(1L);

        // Then
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).delete(doctor);
    }

    @Test
    void deleteDoctorByIdShouldThrowResourceNotFoundExceptionWhenNotFound() {
        // Given
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> doctorService.deleteDoctorById(999L));
        verify(doctorRepository, times(1)).findById(999L);
        verify(doctorRepository, never()).delete(any(Doctor.class));
    }

    @Test
    void deleteDoctorByIdShouldThrowConflictExceptionWhenDoctorHasShifts() {
        // Given
        Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        doctor.getShifts().add(new com.oot.clinic.entities.Shift());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // When & Then
        assertThrows(ConflictException.class, () -> doctorService.deleteDoctorById(1L));
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, never()).delete(any(Doctor.class));
    }
}

