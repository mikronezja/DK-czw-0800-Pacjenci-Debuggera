package com.oot.clinic.doctor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/add")
    public void addDoctor(@RequestBody Doctor doctor) {
        doctorService.addDoctor(
                doctor.getName(),
                doctor.getSurname(),
                doctor.getPesel(),
                doctor.getSpecialization(),
                doctor.getAddress()
        );
    }

    @GetMapping
    public List<Doctor> getDoctors() {
        return doctorService.getDoctors();
    }
}

