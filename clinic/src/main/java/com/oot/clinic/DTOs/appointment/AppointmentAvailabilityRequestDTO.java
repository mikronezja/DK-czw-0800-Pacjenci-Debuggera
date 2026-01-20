package com.oot.clinic.DTOs.appointment;

import com.oot.clinic.entities.enumeration.Specialization;

import java.time.LocalDate;

public record AppointmentAvailabilityRequestDTO(
        LocalDate date,
        Specialization specialization
) {}
