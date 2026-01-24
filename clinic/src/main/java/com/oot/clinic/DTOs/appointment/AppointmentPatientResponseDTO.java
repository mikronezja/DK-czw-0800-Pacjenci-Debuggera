package com.oot.clinic.DTOs.appointment;

import com.oot.clinic.DTOs.doctor.DoctorDTO;
import com.oot.clinic.entities.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentPatientResponseDTO {

    private final Long id;
    private final DoctorDTO doctor;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public AppointmentPatientResponseDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.doctor = new DoctorDTO(appointment.getDoctor());
        this.date = appointment.getDate();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
    }

    // GETTERS

    public Long getId() {
        return id;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
