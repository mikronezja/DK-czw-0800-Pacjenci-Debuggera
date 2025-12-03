package com.oot.clinic.doctor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private final List<Doctor> doctors = new ArrayList<>();

    public void addDoctor(String firstName, String lastName, String pesel,
                          String specialization, String address) {
        doctors.add(new Doctor(firstName, lastName, pesel, specialization, address));
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }
}
