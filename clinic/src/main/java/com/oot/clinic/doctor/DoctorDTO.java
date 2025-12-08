package com.oot.clinic.doctor;


public class DoctorDTO {

    private final String name;
    private final String surname;
    private final String specialization;
    private Long id;

    DoctorDTO(Doctor doctor) {
        this.name = doctor.getName();
        this.surname = doctor.getSurname();
        this.specialization = doctor.getSpecialization();
        this.id = doctor.getId();
    }

    // GETTERS

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Long getId() {
        return id;
    }
}
