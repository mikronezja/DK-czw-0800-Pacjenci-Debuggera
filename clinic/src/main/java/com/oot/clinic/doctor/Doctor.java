package com.oot.clinic.doctor;

public class Doctor {

    private String name;
    private String surname;
    private String pesel;
    private String specialization;
    private String address;

    public Doctor(String name, String surname, String pesel, String specialization, String address) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.specialization = specialization;
        this.address = address;
    }

    public Doctor() {
    }

    // GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPesel() {
        return pesel;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
