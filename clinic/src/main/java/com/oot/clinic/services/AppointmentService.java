package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentResponseDTO;
import com.oot.clinic.DTOs.doctor.DoctorAvailabilityDTO;
import com.oot.clinic.DTOs.other.TimeRangeDTO;
import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.repositories.AppointmentRepository;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.PatientRepository;
import com.oot.clinic.repositories.ShiftRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.List.copyOf;
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
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacjent", patientId));

        Appointment appointment = appointmentRepository.save(new Appointment(doctor, patient, date, startTime, endTime));

        return new AppointmentResponseDTO(appointment);
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
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        List<Shift> shifts = shiftRepository.findShiftsByDoctorInAndDayOfWeek(doctors, date.getDayOfWeek());
        List<Appointment> appointments = appointmentRepository.findByDoctorInAndDate(doctors, date);

        Map<Long, List<Shift>> shiftsByDoctor = shifts.stream()
                .collect(groupingBy(s -> s.getDoctor().getId()));
        Map<Long, List<Appointment>> appointmentsByDoctor = appointments.stream()
                .collect(groupingBy(a -> a.getDoctor().getId()));

        List<DoctorAvailabilityDTO> availabilities = new ArrayList<>();

        for (Doctor doctor : doctors) {
            List<Shift> doctorShifts = shiftsByDoctor.getOrDefault(doctor.getId(), List.of());
            if (doctorShifts.isEmpty()) {
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

        return availabilities;
    }
}
