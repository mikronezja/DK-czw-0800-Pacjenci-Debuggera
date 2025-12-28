package com.oot.clinic.DTOs;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Shift;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ShiftOfficeResponseDTO {

    private DoctorDTO doctor;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public ShiftOfficeResponseDTO(Shift shift) {
        this.doctor = new DoctorDTO(shift.getDoctor());
        this.dayOfWeek = shift.getDayOfWeek();
        this.startTime = shift.getStartTime();
        this.endTime = shift.getEndTime();
    }

    // GETTERS AND SETTERS

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
