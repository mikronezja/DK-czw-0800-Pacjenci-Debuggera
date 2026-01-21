package com.oot.clinic.DTOs.shift;

import com.oot.clinic.DTOs.office.OfficeResponseDTO;
import com.oot.clinic.entities.Shift;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ShiftDoctorResponseDTO {

    private Long id;
    private OfficeResponseDTO office;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public ShiftDoctorResponseDTO(Shift shift) {
        this.id = shift.getId();
        this.office = new OfficeResponseDTO(shift.getOffice());
        this.dayOfWeek = shift.getDayOfWeek();
        this.startTime = shift.getStartTime();
        this.endTime = shift.getEndTime();
    }

    // GETTERS AND SETTERS

    public OfficeResponseDTO getOffice() {
        return office;
    }

    public void setOffice(OfficeResponseDTO office) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
