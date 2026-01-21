package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentResponseDTO;
import com.oot.clinic.DTOs.doctor.DoctorAvailabilityDTO;
import com.oot.clinic.DTOs.other.TimeRangeDTO;
import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.exceptions.InavailabilityException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.AppointmentRepository;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.PatientRepository;
import com.oot.clinic.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTests {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor doctor;
    private Patient patient;
    private Office office;
    private Shift shift;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        doctor = new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Warszawa");
        setId(doctor, 1L);

        patient = new Patient("Anna", "Nowak", "Kraków", "90020254321");
        setId(patient, 1L);

        office = new Office(101);

        futureDate = LocalDate.now().plusDays(7);
        DayOfWeek futureDayOfWeek = futureDate.getDayOfWeek();

        shift = new Shift(doctor, office, futureDayOfWeek, LocalTime.of(8, 0), LocalTime.of(16, 0));
        setId(shift, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Appointment createAppointment(Doctor doctor, Patient patient, LocalDate date,
                                          LocalTime startTime, LocalTime endTime) {
        Appointment appointment = new Appointment(doctor, patient, date, startTime, endTime);
        setId(appointment, 1L);
        return appointment;
    }

    // ==================== CREATE APPOINTMENT TESTS ====================

    @Nested
    @DisplayName("createAppointment")
    class CreateAppointmentTests {

        @Test
        @DisplayName("powinno stworzyć wizytę gdy lekarz jest dostępny")
        void shouldCreateAppointmentWhenDoctorIsAvailable() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(9, 0), LocalTime.of(10, 0));

            // Then
            assertNotNull(result);
            assertEquals("Jan", result.getDoctor().getName());
            assertEquals("Anna", result.getPatient().getName());
            assertEquals(LocalTime.of(9, 0), result.getStartTime());
            assertEquals(LocalTime.of(10, 0), result.getEndTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy godzina rozpoczęcia jest po godzinie zakończenia")
        void shouldThrowValidationExceptionWhenStartTimeIsAfterEndTime() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(14, 0), LocalTime.of(10, 0)));

            assertEquals("Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia", exception.getMessage());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy godzina rozpoczęcia równa się godzinie zakończenia")
        void shouldThrowValidationExceptionWhenStartTimeEqualsEndTime() {
            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(10, 0), LocalTime.of(10, 0)));

            assertEquals("Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia", exception.getMessage());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy lekarz nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenDoctorNotFound() {
            // Given
            when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    appointmentService.createAppointment(999L, 1L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy pacjent nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenPatientNotFound() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    appointmentService.createAppointment(1L, 999L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy lekarz nie ma dyżuru w danym terminie")
        void shouldThrowInavailabilityExceptionWhenDoctorHasNoShift() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of());

            // When & Then
            assertThrows(InavailabilityException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy wizyta wykracza poza godziny dyżuru")
        void shouldThrowInavailabilityExceptionWhenAppointmentOutsideShiftHours() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());

            // When & Then - wizyta przed dyżurem
            assertThrows(InavailabilityException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(6, 0), LocalTime.of(7, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy wizyta kończy się po zakończeniu dyżuru")
        void shouldThrowInavailabilityExceptionWhenAppointmentEndsAfterShift() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());

            // When & Then - wizyta kończy się po 16:00
            assertThrows(InavailabilityException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(15, 0), LocalTime.of(17, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno stworzyć wizytę na początku dyżuru")
        void shouldCreateAppointmentAtShiftStart() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(8, 0), LocalTime.of(9, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(8, 0), result.getStartTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno stworzyć wizytę na końcu dyżuru")
        void shouldCreateAppointmentAtShiftEnd() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(15, 0), LocalTime.of(16, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(16, 0), result.getEndTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy termin jest zajęty przez inną wizytę")
        void shouldThrowInavailabilityExceptionWhenSlotIsTaken() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));

            // When & Then - próba umówienia wizyty w tym samym czasie
            assertThrows(InavailabilityException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nowa wizyta nakłada się na istniejącą")
        void shouldThrowInavailabilityExceptionWhenAppointmentOverlaps() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(10, 0), LocalTime.of(12, 0));

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));

            // When & Then - nowa wizyta nakłada się częściowo
            assertThrows(InavailabilityException.class, () ->
                    appointmentService.createAppointment(1L, 1L, futureDate,
                            LocalTime.of(11, 0), LocalTime.of(13, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno stworzyć wizytę przed istniejącą wizytą")
        void shouldCreateAppointmentBeforeExistingOne() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(12, 0), LocalTime.of(13, 0));

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 2L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(9, 0), LocalTime.of(10, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(9, 0), result.getStartTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno stworzyć wizytę po istniejącej wizycie")
        void shouldCreateAppointmentAfterExistingOne() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 2L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(14, 0), LocalTime.of(15, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(14, 0), result.getStartTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno stworzyć wizytę dokładnie pomiędzy dwoma istniejącymi")
        void shouldCreateAppointmentBetweenTwoExisting() {
            // Given
            Appointment appointment1 = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));
            setId(appointment1, 1L);
            Appointment appointment2 = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(12, 0), LocalTime.of(13, 0));
            setId(appointment2, 2L);

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(appointment1, appointment2));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 3L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(10, 0), LocalTime.of(12, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(10, 0), result.getStartTime());
            assertEquals(LocalTime.of(12, 0), result.getEndTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }
    }

    // ==================== AVAILABLE APPOINTMENTS TESTS ====================

    @Nested
    @DisplayName("availableAppointments")
    class AvailableAppointmentsTests {

        @Test
        @DisplayName("powinno zwrócić pełną dostępność gdy brak wizyt")
        void shouldReturnFullAvailabilityWhenNoAppointments() {
            // Given
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(1, result.size());
            DoctorAvailabilityDTO availability = result.get(0);
            assertEquals(1L, availability.getDoctorId());
            assertEquals("Jan Kowalski", availability.getDoctorName());
            assertEquals(1, availability.getTimeRanges().size());
            assertEquals(LocalTime.of(8, 0), availability.getTimeRanges().get(0).startTime());
            assertEquals(LocalTime.of(16, 0), availability.getTimeRanges().get(0).endTime());
        }

        @Test
        @DisplayName("powinno rzucić wyjątek dla daty z przeszłości")
        void shouldThrowValidationExceptionForPastDate() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(1);

            // When & Then
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    appointmentService.availableAppointments(pastDate, Specialization.KARDIOLOG));

            assertEquals("Data jest z przeszłości.", exception.getMessage());
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy brak lekarzy danej specjalizacji")
        void shouldThrowInavailabilityExceptionWhenNoSpecialists() {
            // Given
            when(doctorRepository.findBySpecialization(Specialization.NEUROLOG)).thenReturn(List.of());

            // When & Then
            InavailabilityException exception = assertThrows(InavailabilityException.class, () ->
                    appointmentService.availableAppointments(futureDate, Specialization.NEUROLOG));

            assertEquals("Nie ma lekarzy tej specjalizacji.", exception.getMessage());
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy brak dostępnych specjalistów w danym dniu")
        void shouldThrowInavailabilityExceptionWhenNoAvailableSpecialists() {
            // Given
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of());

            // When & Then
            InavailabilityException exception = assertThrows(InavailabilityException.class, () ->
                    appointmentService.availableAppointments(futureDate, Specialization.KARDIOLOG));

            assertEquals("Nie ma dostępnych specjalistów dla tego terminu.", exception.getMessage());
        }

        @Test
        @DisplayName("powinno podzielić dostępność gdy wizyta jest w środku dyżuru")
        void shouldSplitAvailabilityWhenAppointmentInMiddle() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(11, 0), LocalTime.of(12, 0));

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(1, result.size());
            List<TimeRangeDTO> ranges = result.get(0).getTimeRanges();
            assertEquals(2, ranges.size());

            // Pierwszy slot: 8:00-11:00
            assertEquals(LocalTime.of(8, 0), ranges.get(0).startTime());
            assertEquals(LocalTime.of(11, 0), ranges.get(0).endTime());

            // Drugi slot: 12:00-16:00
            assertEquals(LocalTime.of(12, 0), ranges.get(1).startTime());
            assertEquals(LocalTime.of(16, 0), ranges.get(1).endTime());
        }

        @Test
        @DisplayName("powinno zmniejszyć dostępność gdy wizyta jest na początku dyżuru")
        void shouldReduceAvailabilityWhenAppointmentAtStart() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(8, 0), LocalTime.of(10, 0));

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(1, result.size());
            List<TimeRangeDTO> ranges = result.get(0).getTimeRanges();
            assertEquals(1, ranges.size());
            assertEquals(LocalTime.of(10, 0), ranges.get(0).startTime());
            assertEquals(LocalTime.of(16, 0), ranges.get(0).endTime());
        }

        @Test
        @DisplayName("powinno zmniejszyć dostępność gdy wizyta jest na końcu dyżuru")
        void shouldReduceAvailabilityWhenAppointmentAtEnd() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(14, 0), LocalTime.of(16, 0));

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(1, result.size());
            List<TimeRangeDTO> ranges = result.get(0).getTimeRanges();
            assertEquals(1, ranges.size());
            assertEquals(LocalTime.of(8, 0), ranges.get(0).startTime());
            assertEquals(LocalTime.of(14, 0), ranges.get(0).endTime());
        }

        @Test
        @DisplayName("powinno obsłużyć wiele wizyt w jednym dniu")
        void shouldHandleMultipleAppointmentsInOneDay() {
            // Given
            Appointment appointment1 = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));
            setId(appointment1, 1L);
            Appointment appointment2 = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(12, 0), LocalTime.of(13, 0));
            setId(appointment2, 2L);
            Appointment appointment3 = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(14, 0), LocalTime.of(15, 0));
            setId(appointment3, 3L);

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any()))
                    .thenReturn(List.of(appointment1, appointment2, appointment3));

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(1, result.size());
            List<TimeRangeDTO> ranges = result.get(0).getTimeRanges();
            assertEquals(4, ranges.size());

            // 8:00-9:00
            assertEquals(LocalTime.of(8, 0), ranges.get(0).startTime());
            assertEquals(LocalTime.of(9, 0), ranges.get(0).endTime());

            // 10:00-12:00
            assertEquals(LocalTime.of(10, 0), ranges.get(1).startTime());
            assertEquals(LocalTime.of(12, 0), ranges.get(1).endTime());

            // 13:00-14:00
            assertEquals(LocalTime.of(13, 0), ranges.get(2).startTime());
            assertEquals(LocalTime.of(14, 0), ranges.get(2).endTime());

            // 15:00-16:00
            assertEquals(LocalTime.of(15, 0), ranges.get(3).startTime());
            assertEquals(LocalTime.of(16, 0), ranges.get(3).endTime());
        }

        @Test
        @DisplayName("powinno zwrócić dostępność wielu lekarzy")
        void shouldReturnAvailabilityForMultipleDoctors() {
            // Given
            Doctor doctor2 = new Doctor("Maria", "Wiśniewska", "85050598765", Specialization.KARDIOLOG, "Poznań");
            setId(doctor2, 2L);
            Shift shift2 = new Shift(doctor2, office, futureDate.getDayOfWeek(),
                    LocalTime.of(10, 0), LocalTime.of(18, 0));
            setId(shift2, 2L);

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor, doctor2));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift, shift2));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("powinno zwrócić lekarza z pustą listą dostępności gdy wizyta pokrywa cały dyżur")
        void shouldReturnDoctorWithEmptyAvailabilityWhenAppointmentCoversEntireShift() {
            // Given
            Appointment fullDayAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(8, 0), LocalTime.of(16, 0));

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(fullDayAppointment));

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then - lekarz jest zwrócony, ale z pustą listą dostępnych terminów
            assertEquals(1, result.size());
            assertTrue(result.get(0).getTimeRanges().isEmpty());
        }

        @Test
        @DisplayName("powinno pozwolić na wizytę w dniu dzisiejszym jeśli nie minął")
        void shouldAllowAppointmentToday() {
            // Given
            LocalDate today = LocalDate.now();
            DayOfWeek todayDayOfWeek = today.getDayOfWeek();
            Shift todayShift = new Shift(doctor, office, todayDayOfWeek, LocalTime.of(8, 0), LocalTime.of(20, 0));
            setId(todayShift, 10L);

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), eq(todayDayOfWeek))).thenReturn(List.of(todayShift));
            when(appointmentRepository.findByDoctorInAndDate(any(), eq(today))).thenReturn(List.of());

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    today, Specialization.KARDIOLOG);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    // ==================== UPDATE APPOINTMENT TESTS ====================

    @Nested
    @DisplayName("updateAppointment")
    class UpdateAppointmentTests {

        @Test
        @DisplayName("powinno zaktualizować wizytę")
        void shouldUpdateAppointment() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));

            Patient newPatient = new Patient("Tomasz", "Zieliński", "Gdańsk", "75030312345");
            setId(newPatient, 2L);

            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(2L)).thenReturn(Optional.of(newPatient));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Appointment result = appointmentService.updateAppointment(
                    1L, 1L, 2L, futureDate.plusDays(1), LocalTime.of(11, 0), LocalTime.of(12, 0));

            // Then
            assertNotNull(result);
            assertEquals(newPatient, result.getPatient());
            assertEquals(futureDate.plusDays(1), result.getDate());
            assertEquals(LocalTime.of(11, 0), result.getStartTime());
            assertEquals(LocalTime.of(12, 0), result.getEndTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy wizyta nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenAppointmentNotFound() {
            // Given
            when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    appointmentService.updateAppointment(999L, 1L, 1L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nowy lekarz nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenNewDoctorNotFound() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));

            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
            when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    appointmentService.updateAppointment(1L, 999L, 1L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno rzucić wyjątek gdy nowy pacjent nie istnieje")
        void shouldThrowResourceNotFoundExceptionWhenNewPatientNotFound() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));

            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    appointmentService.updateAppointment(1L, 1L, 999L, futureDate,
                            LocalTime.of(9, 0), LocalTime.of(10, 0)));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }
    }

    // ==================== DELETE APPOINTMENT TESTS ====================

    @Nested
    @DisplayName("deleteAppointment")
    class DeleteAppointmentTests {

        @Test
        @DisplayName("powinno usunąć wizytę")
        void shouldDeleteAppointment() {
            // Given
            doNothing().when(appointmentRepository).deleteById(1L);

            // When
            appointmentService.deleteAppointment(1L);

            // Then
            verify(appointmentRepository, times(1)).deleteById(1L);
        }
    }

    // ==================== GET ALL APPOINTMENTS TESTS ====================

    @Nested
    @DisplayName("getAllAppointments")
    class GetAllAppointmentsTests {

        @Test
        @DisplayName("powinno zwrócić listę wszystkich wizyt")
        void shouldReturnAllAppointments() {
            // Given
            Appointment appointment1 = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));
            Appointment appointment2 = createAppointment(doctor, patient, futureDate.plusDays(1),
                    LocalTime.of(11, 0), LocalTime.of(12, 0));
            setId(appointment2, 2L);

            when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

            // When
            List<Appointment> result = appointmentService.getAllAppointments();

            // Then
            assertEquals(2, result.size());
            verify(appointmentRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("powinno zwrócić pustą listę gdy brak wizyt")
        void shouldReturnEmptyListWhenNoAppointments() {
            // Given
            when(appointmentRepository.findAll()).thenReturn(List.of());

            // When
            List<Appointment> result = appointmentService.getAllAppointments();

            // Then
            assertTrue(result.isEmpty());
            verify(appointmentRepository, times(1)).findAll();
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("powinno obsłużyć wizyty stykające się czasowo")
        void shouldHandleBackToBackAppointments() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(10, 0), LocalTime.of(11, 0));

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 2L);
                return saved;
            });

            // When - wizyta zaczynająca się dokładnie gdy poprzednia się kończy
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(11, 0), LocalTime.of(12, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(11, 0), result.getStartTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno obsłużyć wizytę kończącą się tuż przed następną")
        void shouldHandleAppointmentEndingJustBeforeNext() {
            // Given
            Appointment existingAppointment = createAppointment(doctor, patient, futureDate,
                    LocalTime.of(12, 0), LocalTime.of(13, 0));

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of(existingAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 2L);
                return saved;
            });

            // When - wizyta kończąca się dokładnie gdy następna zaczyna
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(11, 0), LocalTime.of(12, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(12, 0), result.getEndTime());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("powinno obsłużyć bardzo krótką wizytę (1 minuta)")
        void shouldHandleVeryShortAppointment() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(10, 0), LocalTime.of(10, 1));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(10, 0), result.getStartTime());
            assertEquals(LocalTime.of(10, 1), result.getEndTime());
        }

        @Test
        @DisplayName("powinno obsłużyć wizytę trwającą cały dyżur")
        void shouldHandleFullShiftAppointment() {
            // Given
            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any())).thenReturn(List.of(shift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment saved = invocation.getArgument(0);
                setId(saved, 1L);
                return saved;
            });

            // When
            AppointmentResponseDTO result = appointmentService.createAppointment(
                    1L, 1L, futureDate, LocalTime.of(8, 0), LocalTime.of(16, 0));

            // Then
            assertNotNull(result);
            assertEquals(LocalTime.of(8, 0), result.getStartTime());
            assertEquals(LocalTime.of(16, 0), result.getEndTime());
        }

        @Test
        @DisplayName("powinno obsłużyć lekarza z wieloma dyżurami w jednym dniu")
        void shouldHandleDoctorWithMultipleShiftsInOneDay() {
            // Given - lekarz ma dyżur rano i wieczorem
            Shift morningShift = new Shift(doctor, office, futureDate.getDayOfWeek(),
                    LocalTime.of(8, 0), LocalTime.of(12, 0));
            setId(morningShift, 1L);
            Shift eveningShift = new Shift(doctor, office, futureDate.getDayOfWeek(),
                    LocalTime.of(16, 0), LocalTime.of(20, 0));
            setId(eveningShift, 2L);

            when(doctorRepository.findBySpecialization(Specialization.KARDIOLOG)).thenReturn(List.of(doctor));
            when(shiftRepository.findShiftsByDoctorInAndDayOfWeek(any(), any()))
                    .thenReturn(List.of(morningShift, eveningShift));
            when(appointmentRepository.findByDoctorInAndDate(any(), any())).thenReturn(List.of());

            // When
            List<DoctorAvailabilityDTO> result = appointmentService.availableAppointments(
                    futureDate, Specialization.KARDIOLOG);

            // Then
            assertEquals(1, result.size());
            List<TimeRangeDTO> ranges = result.get(0).getTimeRanges();
            assertEquals(2, ranges.size());

            // Poranny dyżur
            assertEquals(LocalTime.of(8, 0), ranges.get(0).startTime());
            assertEquals(LocalTime.of(12, 0), ranges.get(0).endTime());

            // Wieczorny dyżur
            assertEquals(LocalTime.of(16, 0), ranges.get(1).startTime());
            assertEquals(LocalTime.of(20, 0), ranges.get(1).endTime());
        }
    }
}
