package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentPatientResponseDTO;
import com.oot.clinic.DTOs.patient.PatientDTO;
import com.oot.clinic.DTOs.patient.PatientResponseDTO;
import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTests {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== ADD PATIENT TESTS ====================

    @Nested
    @DisplayName("addPatient")
    class AddPatientTests {

        @Test
        @DisplayName("powinno dodać pacjenta z wszystkimi danymi")
        void shouldAddPatientWithAllData() {
            // Given
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Nowak", "90020254321", "Kraków");

            // Then
            assertNotNull(result);
            assertEquals("Anna", result.getName());
            assertEquals("Nowak", result.getSurname());
            assertEquals("90020254321", result.getPesel());
            assertEquals("Kraków", result.getAddress());
            verify(patientRepository, times(1)).findByPesel("90020254321");
            verify(patientRepository, times(1)).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno dodać pacjenta bez PESEL")
        void shouldAddPatientWithoutPesel() {
            // Given
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Nowak", null, "Kraków");

            // Then
            assertNotNull(result);
            assertEquals("Anna", result.getName());
            assertEquals("Nowak", result.getSurname());
            assertNull(result.getPesel());
            verify(patientRepository, never()).findByPesel(anyString());
            verify(patientRepository, times(1)).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno dodać pacjenta z pustym PESEL")
        void shouldAddPatientWithEmptyPesel() {
            // Given
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Nowak", "", "Kraków");

            // Then
            assertNotNull(result);
            verify(patientRepository, never()).findByPesel(anyString());
            verify(patientRepository, times(1)).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno dodać pacjenta bez adresu")
        void shouldAddPatientWithoutAddress() {
            // Given
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Nowak", "90020254321", null);

            // Then
            assertNotNull(result);
            assertNull(result.getAddress());
            verify(patientRepository, times(1)).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno przyciąć białe znaki w danych")
        void shouldTrimWhitespaces() {
            // Given
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("  Anna  ", "  Nowak  ", "90020254321", "  Kraków  ");

            // Then
            assertEquals("Anna", result.getName());
            assertEquals("Nowak", result.getSurname());
            assertEquals("Kraków", result.getAddress());
        }

        // ---- VALIDATION TESTS ----

        @Test
        @DisplayName("powinno rzucić wyjątek gdy imię jest null")
        void shouldThrowValidationExceptionWhenNameIsNull() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient(null, "Nowak", "90020254321", "Kraków"));

            assertEquals("Imię jest wymagane", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy imię jest puste")
        void shouldThrowValidationExceptionWhenNameIsEmpty() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("", "Nowak", "90020254321", "Kraków"));

            assertEquals("Imię jest wymagane", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy imię zawiera tylko białe znaki")
        void shouldThrowValidationExceptionWhenNameIsOnlyWhitespace() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("   ", "Nowak", "90020254321", "Kraków"));

            assertEquals("Imię jest wymagane", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nazwisko jest null")
        void shouldThrowValidationExceptionWhenSurnameIsNull() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", null, "90020254321", "Kraków"));

            assertEquals("Nazwisko jest wymagane", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nazwisko jest puste")
        void shouldThrowValidationExceptionWhenSurnameIsEmpty() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "", "90020254321", "Kraków"));

            assertEquals("Nazwisko jest wymagane", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nazwisko zawiera tylko białe znaki")
        void shouldThrowValidationExceptionWhenSurnameIsOnlyWhitespace() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "   ", "90020254321", "Kraków"));

            assertEquals("Nazwisko jest wymagane", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy imię jest za długie")
        void shouldThrowValidationExceptionWhenNameIsTooLong() {
            // Given
            String longName = "x".repeat(101);

            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient(longName, "Nowak", "90020254321", "Kraków"));

            assertEquals("Imię jest za długie (maks. 100 znaków)", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nazwisko jest za długie")
        void shouldThrowValidationExceptionWhenSurnameIsTooLong() {
            // Given
            String longSurname = "x".repeat(101);

            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", longSurname, "90020254321", "Kraków"));

            assertEquals("Nazwisko jest za długie (maks. 100 znaków)", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy adres jest za długi")
        void shouldThrowValidationExceptionWhenAddressIsTooLong() {
            // Given
            String longAddress = "x".repeat(201);

            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "Nowak", "90020254321", longAddress));

            assertEquals("Adres jest za długi (maks. 200 znaków)", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy PESEL ma nieprawidłową długość")
        void shouldThrowValidationExceptionWhenPeselHasInvalidLength() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "Nowak", "12345", "Kraków"));

            assertEquals("PESEL musi mieć dokładnie 11 cyfr", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy PESEL jest za długi")
        void shouldThrowValidationExceptionWhenPeselIsTooLong() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "Nowak", "123456789012", "Kraków"));

            assertEquals("PESEL musi mieć dokładnie 11 cyfr", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy PESEL zawiera litery")
        void shouldThrowValidationExceptionWhenPeselContainsLetters() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "Nowak", "9002025432a", "Kraków"));

            assertEquals("PESEL może zawierać tylko cyfry", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy PESEL zawiera znaki specjalne")
        void shouldThrowValidationExceptionWhenPeselContainsSpecialChars() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    patientService.addPatient("Anna", "Nowak", "90020254-21", "Kraków"));

            assertEquals("PESEL może zawierać tylko cyfry", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy PESEL już istnieje")
        void shouldThrowConflictExceptionWhenPeselExists() {
            // Given
            Patient existingPatient = new Patient("Jan", "Kowalski", "Warszawa", "90020254321");
            when(patientRepository.findByPesel("90020254321")).thenReturn(Optional.of(existingPatient));

            // When & Then
            ConflictException exception = assertThrows(ConflictException.class, () ->
                    patientService.addPatient("Anna", "Nowak", "90020254321", "Kraków"));

            assertEquals("Pacjent z tym numerem PESEL już istnieje", exception.getMessage());
            verify(patientRepository, times(1)).findByPesel("90020254321");
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno zaakceptować imię o długości dokładnie 100 znaków")
        void shouldAcceptNameWithExactly100Characters() {
            // Given
            String name100Chars = "x".repeat(100);
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient(name100Chars, "Nowak", "90020254321", "Kraków");

            // Then
            assertNotNull(result);
            assertEquals(100, result.getName().length());
            verify(patientRepository, times(1)).save(any(Patient.class));
        }

        @Test
        @DisplayName("powinno zaakceptować adres o długości dokładnie 200 znaków")
        void shouldAcceptAddressWithExactly200Characters() {
            // Given
            String address200Chars = "x".repeat(200);
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Nowak", "90020254321", address200Chars);

            // Then
            assertNotNull(result);
            assertEquals(200, result.getAddress().length());
            verify(patientRepository, times(1)).save(any(Patient.class));
        }
    }

    // ==================== GET PATIENTS TESTS ====================

    @Nested
    @DisplayName("getPatients")
    class GetPatientsTests {

        @Test
        @DisplayName("powinno zwrócić listę wszystkich pacjentów")
        void shouldReturnAllPatients() {
            // Given
            Patient patient1 = new Patient("Anna", "Nowak", "Kraków", "90020254321");
            setId(patient1, 1L);
            Patient patient2 = new Patient("Jan", "Kowalski", "Warszawa", "80010112345");
            setId(patient2, 2L);
            when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

            // When
            List<PatientDTO> result = patientService.getPatients();

            // Then
            assertEquals(2, result.size());
            assertEquals("Anna", result.get(0).getName());
            assertEquals("Jan", result.get(1).getName());
            verify(patientRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("powinno zwrócić pustą listę gdy brak pacjentów")
        void shouldReturnEmptyListWhenNoPatients() {
            // Given
            when(patientRepository.findAll()).thenReturn(List.of());

            // When
            List<PatientDTO> result = patientService.getPatients();

            // Then
            assertTrue(result.isEmpty());
            verify(patientRepository, times(1)).findAll();
        }
    }

    // ==================== GET PATIENT BY ID TESTS ====================

    @Nested
    @DisplayName("getPatientById")
    class GetPatientByIdTests {

        @Test
        @DisplayName("powinno zwrócić pacjenta po ID")
        void shouldReturnPatientById() {
            // Given
            Patient patient = new Patient("Anna", "Nowak", "Kraków", "90020254321");
            setId(patient, 1L);
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

            // When
            Patient result = patientService.getPatientById(1L);

            // Then
            assertNotNull(result);
            assertEquals("Anna", result.getName());
            assertEquals("Nowak", result.getSurname());
            verify(patientRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy pacjent nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenPatientNotFound() {
            // Given
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(999L));
            verify(patientRepository, times(1)).findById(999L);
        }
    }

    // ==================== DELETE PATIENT TESTS ====================

    @Nested
    @DisplayName("deletePatientById")
    class DeletePatientByIdTests {

        @Test
        @DisplayName("powinno usunąć pacjenta")
        void shouldDeletePatient() {
            // Given
            Patient patient = new Patient("Anna", "Nowak", "Kraków", "90020254321");
            setId(patient, 1L);
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            doNothing().when(patientRepository).delete(any(Patient.class));

            // When
            patientService.deletePatientById(1L);

            // Then
            verify(patientRepository, times(1)).findById(1L);
            verify(patientRepository, times(1)).delete(patient);
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy pacjent do usunięcia nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenPatientToDeleteNotFound() {
            // Given
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatientById(999L));
            verify(patientRepository, times(1)).findById(999L);
            verify(patientRepository, never()).delete(any(Patient.class));
        }
    }

    // ==================== GET APPOINTMENTS TESTS ====================

    @Nested
    @DisplayName("getAppointments")
    class GetAppointmentsTests {

        @Test
        @DisplayName("powinno zwrócić listę wizyt pacjenta")
        void shouldReturnPatientAppointments() {
            // Given
            Patient patient = new Patient("Anna", "Nowak", "Kraków", "90020254321");
            setId(patient, 1L);

            Doctor doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
            setId(doctor, 1L);

            Appointment appointment1 = new Appointment(doctor, patient, LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0), LocalTime.of(10, 0));
            Appointment appointment2 = new Appointment(doctor, patient, LocalDate.now().plusDays(2),
                    LocalTime.of(11, 0), LocalTime.of(12, 0));

            patient.getAppointments().add(appointment1);
            patient.getAppointments().add(appointment2);

            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

            // When
            List<AppointmentPatientResponseDTO> result = patientService.getAppointments(1L);

            // Then
            assertEquals(2, result.size());
            assertEquals(LocalTime.of(9, 0), result.get(0).getStartTime());
            assertEquals(LocalTime.of(11, 0), result.get(1).getStartTime());
            verify(patientRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("powinno zwrócić pustą listę gdy pacjent nie ma wizyt")
        void shouldReturnEmptyListWhenPatientHasNoAppointments() {
            // Given
            Patient patient = new Patient("Anna", "Nowak", "Kraków", "90020254321");
            setId(patient, 1L);
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

            // When
            List<AppointmentPatientResponseDTO> result = patientService.getAppointments(1L);

            // Then
            assertTrue(result.isEmpty());
            verify(patientRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy pacjent nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenPatientNotFound() {
            // Given
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> patientService.getAppointments(999L));
            verify(patientRepository, times(1)).findById(999L);
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("powinno obsłużyć polskie znaki w danych pacjenta")
        void shouldHandlePolishCharacters() {
            // Given
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Żółć", "Źródło-Łąka", "90020254321", "Świętokrzyska 15, Łódź");

            // Then
            assertNotNull(result);
            assertEquals("Żółć", result.getName());
            assertEquals("Źródło-Łąka", result.getSurname());
            assertEquals("Świętokrzyska 15, Łódź", result.getAddress());
        }

        @Test
        @DisplayName("powinno obsłużyć nazwisko z myślnikiem")
        void shouldHandleHyphenatedSurname() {
            // Given
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Kowalska-Nowak", "90020254321", "Kraków");

            // Then
            assertNotNull(result);
            assertEquals("Kowalska-Nowak", result.getSurname());
        }

        @Test
        @DisplayName("powinno obsłużyć wielokrotne spacje w danych")
        void shouldHandleMultipleSpaces() {
            // Given
            when(patientRepository.findByPesel(anyString())).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("   Anna   ", "   Nowak   ", "90020254321", "   Kraków   ");

            // Then
            assertNotNull(result);
            assertEquals("Anna", result.getName());
            assertEquals("Nowak", result.getSurname());
            assertEquals("Kraków", result.getAddress());
        }

        @Test
        @DisplayName("powinno obsłużyć PESEL ze spacjami")
        void shouldTrimPeselWhitespace() {
            // Given
            when(patientRepository.findByPesel("90020254321")).thenReturn(Optional.empty());
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            PatientResponseDTO result = patientService.addPatient("Anna", "Nowak", "  90020254321  ", "Kraków");

            // Then
            assertNotNull(result);
            verify(patientRepository, times(1)).findByPesel("90020254321");
        }
    }
}
