package com.oot.clinic.DTOs.appointment;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class AppointmentRequestDTO {

    private Long doctorId;
    private Long patientId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // GETTERS

    public Long getDoctorId() {
        return doctorId;
    }

    public Long getPatientId() {
        return patientId;
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
