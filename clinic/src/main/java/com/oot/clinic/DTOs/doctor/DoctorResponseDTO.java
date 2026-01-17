package com.oot.clinic.DTOs.doctor;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.enumeration.Specialization;

public class DoctorResponseDTO {

    private String name;
    private String surname;
    private String pesel;
    private Specialization specialization;
    private String address;
    private Long id;

    public DoctorResponseDTO(Doctor doctor) {
        this.name = doctor.getName();
        this.surname = doctor.getSurname();
        this.pesel = doctor.getPesel();
        this.specialization = doctor.getSpecialization();
        this.address = doctor.getAddress();
        this.id = doctor.getId();
    }

    // GETTERS AND SETTERS


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
