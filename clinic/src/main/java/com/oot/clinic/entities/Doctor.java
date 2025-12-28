package com.oot.clinic.entities;

import com.oot.clinic.entities.enumeration.Specialization;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Doctor {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String surname;
    private String pesel;
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    private String address;
    @OneToMany(mappedBy = "doctor")
    private List<Shift> shifts = new ArrayList<>();

    public Doctor(String name, String surname, String pesel, Specialization specialization, String address) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.specialization = specialization;
        this.address = address;
    }

    public Doctor() {}

    // GETTERS AND SETTERS

    public Long getId() {
        return id;
    }

    public String getPesel() {
        return pesel;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

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
}