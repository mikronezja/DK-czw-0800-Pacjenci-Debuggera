package com.oot.clinic.doctor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Doctor {

    private String name;
    private String surname;
    private String pesel;
    private String specialization;
    private String address;
    @Id
    @GeneratedValue
    private Long id;

    public Doctor(String name, String surname, String pesel, String specialization, String address) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.specialization = specialization;
        this.address = address;
    }

    public Doctor() {
    }

//    // GETTERS AND SETTERS

    public Long getId() {
        return id;
    }
}
