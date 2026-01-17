package com.oot.clinic.DTOs.shift;

import com.oot.clinic.DTOs.doctor.DoctorDTO;
import com.oot.clinic.DTOs.office.OfficeResponseDTO;
import com.oot.clinic.entities.Shift;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ShiftResponseDTO {

    private long id;
    private DoctorDTO doctor;
    private OfficeResponseDTO office;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public ShiftResponseDTO(Shift shift) {
        this.id = shift.getId();
        this.doctor = new DoctorDTO(shift.getDoctor());
        this.office = new OfficeResponseDTO(shift.getOffice());
        this.dayOfWeek = shift.getDayOfWeek();
        this.startTime = shift.getStartTime();
        this.endTime = shift.getEndTime();
    }

    // GETTERS

    public long getId() {
        return id;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public OfficeResponseDTO getOffice() {
        return office;
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
