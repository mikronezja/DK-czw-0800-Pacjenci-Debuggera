package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentResponseDTO;
import com.oot.clinic.DTOs.doctor.DoctorAvailabilityDTO;
import com.oot.clinic.DTOs.other.TimeRangeDTO;
import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Doctor;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ShiftRepository shiftRepository;

    AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, PatientRepository patientRepository,  ShiftRepository shiftRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.shiftRepository = shiftRepository;
    }

    public AppointmentResponseDTO createAppointment(Long doctorId, Long patientId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new ValidationException("Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacjent", patientId));


        DoctorAvailabilityDTO doctorAvailability;
        try {
            List<DoctorAvailabilityDTO> doctorsAvailability = availableAppointments(date, doctor.getSpecialization());
            doctorAvailability = doctorsAvailability.stream()
                    .filter(availability -> Objects.equals(availability.getDoctorId(), doctorId))
                    .toList().getFirst();
        } catch (InavailabilityException e) {
            throw new InavailabilityException("Lekarz nie jest dostępny tego dnia");
        }

        for (TimeRangeDTO timeRange : doctorAvailability.getTimeRanges()) {

            if ((startTime.isAfter(timeRange.startTime()) || startTime.equals(timeRange.startTime()))
                    && (endTime.isBefore(timeRange.endTime()) || endTime.equals(timeRange.endTime()))) {

                Appointment appointment = appointmentRepository.save(new Appointment(doctor, patient, date, startTime, endTime));
                return new AppointmentResponseDTO(appointment);
            }
        }
        throw new InavailabilityException("Lekarz nie jest dostępny dla tego terminu.");
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public Appointment updateAppointment(Long id, Long doctorId, Long patientId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wizyta lekarska", id));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacjent", patientId));

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(date);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments(){
        return appointmentRepository.findAll();
    }

    public List<DoctorAvailabilityDTO> availableAppointments(LocalDate date, Specialization specialization) {
        if(date.isBefore(LocalDate.now())){
            throw new ValidationException("Data jest z przeszłości.");
        }

        List<DoctorAvailabilityDTO> availabilities = new ArrayList<>();

        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        if(doctors.isEmpty()){
            throw new InavailabilityException("Nie ma lekarzy tej specjalizacji.");
        }
        List<Shift> shifts = shiftRepository.findShiftsByDoctorInAndDayOfWeek(doctors, date.getDayOfWeek());
        List<Appointment> appointments = appointmentRepository.findByDoctorInAndDate(doctors, date);

//        System.out.println("Requested data: " + specialization + ", " + date);
//        System.out.println("Found doctors: " + doctors.size());
//        System.out.println("Found shifts: " + shifts.size());
//        System.out.println("Found appointments: " + appointments.size());

        Map<Long, List<Shift>> shiftsByDoctor = shifts.stream()
                .collect(groupingBy(s -> s.getDoctor().getId()));
        Map<Long, List<Appointment>> appointmentsByDoctor = appointments.stream()
                .collect(groupingBy(a -> a.getDoctor().getId()));


        for (Doctor doctor : doctors) {
            List<Shift> doctorShifts = shiftsByDoctor.getOrDefault(doctor.getId(), List.of());
            if (doctorShifts.isEmpty()) {
//                System.out.println("This doctor has no shifts, going onto the next one");
                continue;
            }

            List<Appointment> doctorAppointments = appointmentsByDoctor.getOrDefault(doctor.getId(), List.of());
            List<TimeRangeDTO> availableSlots = new ArrayList<>();

            for (Shift shift : doctorShifts) {
                List<TimeRangeDTO> slots = new ArrayList<>();
                slots.add(new TimeRangeDTO(shift.getStartTime(), shift.getEndTime()));

                for (Appointment appointment : doctorAppointments) {
                    List<TimeRangeDTO> newSlots = new ArrayList<>();

                    for (TimeRangeDTO slot : slots) {
                        if (appointment.getEndTime().isBefore(slot.startTime()) ||
                                appointment.getEndTime().equals(slot.startTime()) ||
                                appointment.getStartTime().isAfter(slot.endTime()) ||
                                appointment.getStartTime().equals(slot.endTime())) {
                            newSlots.add(slot);
                        }
                        else if (appointment.getStartTime().isAfter(slot.startTime()) &&
                                appointment.getEndTime().isBefore(slot.endTime())) {
                            newSlots.add(new TimeRangeDTO(slot.startTime(), appointment.getStartTime()));
                            newSlots.add(new TimeRangeDTO(appointment.getEndTime(), slot.endTime()));
                        }
                        else if (appointment.getStartTime().isBefore(slot.startTime()) ||
                                appointment.getStartTime().equals(slot.startTime())) {
                            if (appointment.getEndTime().isBefore(slot.endTime())) {
                                newSlots.add(new TimeRangeDTO(appointment.getEndTime(), slot.endTime()));
                            }
                        }
                        else if (appointment.getEndTime().isAfter(slot.endTime()) ||
                                appointment.getEndTime().equals(slot.endTime())) {
                            if (appointment.getStartTime().isAfter(slot.startTime())) {
                                newSlots.add(new TimeRangeDTO(slot.startTime(), appointment.getStartTime()));
                            }
                        }
                    }
                    slots = newSlots;
                }
                availableSlots.addAll(slots);
            }

            availabilities.add(new DoctorAvailabilityDTO(
                    doctor.getId(),
                    doctor.getName() + " " + doctor.getSurname(),
                    availableSlots
            ));
        }

        if(availabilities.isEmpty()){
            throw new InavailabilityException("Nie ma dostępnych specjalistów dla tego terminu.");
        }

//        System.out.println("Final availabilities size: " + availabilities.size());

        return availabilities;
    }
}
