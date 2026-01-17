package com.oot.clinic.DTOs.appointment;

import com.oot.clinic.DTOs.doctor.DoctorDTO;
import com.oot.clinic.DTOs.patient.PatientDTO;
import com.oot.clinic.entities.Appointment;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class AppointmentResponseDTO {

    private final DoctorDTO doctor;
    private final PatientDTO patient;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public AppointmentResponseDTO(Appointment appointment) {
        this.doctor = new DoctorDTO(appointment.getDoctor());
        this.patient = new PatientDTO(appointment.getPatient());
        this.dayOfWeek = appointment.getDayOfWeek();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
    }

    // GETTERS

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
