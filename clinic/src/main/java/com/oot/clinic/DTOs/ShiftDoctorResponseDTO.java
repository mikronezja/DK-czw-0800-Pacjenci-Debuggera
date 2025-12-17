package com.oot.clinic.DTOs;

import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Shift;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ShiftDoctorResponseDTO {

    private OfficeDTO office;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public ShiftDoctorResponseDTO(Shift shift) {
        this.office = new OfficeDTO(shift.getOffice());
        this.dayOfWeek = shift.getDayOfWeek();
        this.startTime = shift.getStartTime();
        this.endTime = shift.getEndTime();
    }

    // GETTERS AND SETTERS

    public OfficeDTO getOffice() {
        return office;
    }

    public void setOffice(OfficeDTO office) {
        this.office = office;
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
