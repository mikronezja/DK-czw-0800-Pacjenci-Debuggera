package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentRequestDTO;
import com.oot.clinic.DTOs.appointment.AppointmentResponseDTO;
import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.repositories.AppointmentRepository;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public AppointmentResponseDTO createAppointment(Long doctorId, Long patientId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacjent", patientId));

        Appointment appointment = appointmentRepository.save(new Appointment(doctor, patient, dayOfWeek, startTime, endTime));

        return new AppointmentResponseDTO(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public Appointment updateAppointment(Long id, Long doctorId, Long patientId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wizyta lekarska", id));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacjent", patientId));

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDayOfWeek(dayOfWeek);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments(){
        return appointmentRepository.findAll();
    }
}
