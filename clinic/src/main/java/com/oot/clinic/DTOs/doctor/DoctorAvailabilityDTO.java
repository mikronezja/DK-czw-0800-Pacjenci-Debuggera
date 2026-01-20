package com.oot.clinic.DTOs.doctor;

import com.oot.clinic.DTOs.other.TimeRangeDTO;

import java.util.List;

public class DoctorAvailabilityDTO {

    private Long doctorId;
    private String doctorName;
    List<TimeRangeDTO> timeRanges;


    public DoctorAvailabilityDTO(Long doctorId, String doctorName, List<TimeRangeDTO> timeRanges) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.timeRanges = timeRanges;
    }

    // GETTERS


    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public List<TimeRangeDTO> getTimeRanges() {
        return timeRanges;
    }
}
