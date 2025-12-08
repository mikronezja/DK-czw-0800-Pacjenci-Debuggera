package com.oot.clinic.doctor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor addDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<DoctorDTO> getDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorDTO::new)
                .toList();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public boolean deleteDoctorById(Long id) {
        if (!doctorRepository.existsById(id)) {
            return false;
        }
        doctorRepository.deleteById(id);
        return true;
    }
}
