package com.oot.clinic.doctor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {
    private final List<Doctor> doctors = new ArrayList<>();

    public List<Doctor> getDoctors() {
        return doctors;
    }
}
