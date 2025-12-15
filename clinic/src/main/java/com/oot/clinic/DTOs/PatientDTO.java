package com.oot.clinic.DTOs;

import com.oot.clinic.entities.Patient;

public class PatientDTO {

    private final String name;
    private final String surname;
    private final Long id;

    public PatientDTO(Patient patient) {
        this.name = patient.getName();
        this.surname = patient.getSurname();
        this.id = patient.getId();
    }

    // GETTERS

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Long getId() {
        return id;
    }
}
