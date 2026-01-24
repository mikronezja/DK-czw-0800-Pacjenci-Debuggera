package com.oot.clinic.DTOs.appointment;

import com.oot.clinic.DTOs.patient.PatientDTO;
import com.oot.clinic.entities.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDoctorResponseDTO {

    private final PatientDTO patient;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public AppointmentDoctorResponseDTO(Appointment appointment) {
        this.patient = new PatientDTO(appointment.getPatient());
        this.date = appointment.getDate();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
    }

    // GETTERS

    public PatientDTO getPatient() {
        return patient;
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
