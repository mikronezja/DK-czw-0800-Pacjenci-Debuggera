package com.oot.clinic.doctor;

public class DoctorDetailsDto {
    private final Long id;
    private final String name;
    private final String surname;
    private final Specialization specialization;
    private final String address;

    public DoctorDetailsDto(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.surname = doctor.getSurname();
        this.specialization = doctor.getSpecialization();
        this.address = doctor.getAddress();
    }

    // GETTERS

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public String getAddress() {
        return address;
    }
}

