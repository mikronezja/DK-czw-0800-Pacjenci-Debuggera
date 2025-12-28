package com.oot.clinic.services;

import com.oot.clinic.DTOs.ShiftDoctorResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.DTOs.DoctorDTO;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.repositories.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Adds a new doctor to the database
     * @param doctor the Doctor object that's being added
     * @throws RuntimeException if PESEL already exists or fields are too long
     */
    public Doctor addDoctor(Doctor doctor) {
        // Validate required fields
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (doctor.getSurname() == null || doctor.getSurname().trim().isEmpty()) {
            throw new RuntimeException("Surname is required");
        }
        if (doctor.getSpecialization() == null) {
            throw new RuntimeException("Specialization is required");
        }
        
        // Trim whitespace
        doctor.setName(doctor.getName().trim());
        doctor.setSurname(doctor.getSurname().trim());
        if (doctor.getAddress() != null) doctor.setAddress(doctor.getAddress().trim());
        if (doctor.getPesel() != null) doctor.setPesel(doctor.getPesel().trim());
        
        // Validate field lengths
        if (doctor.getName().length() > 100) {
            throw new RuntimeException("Name is too long (max 100 characters)");
        }
        if (doctor.getSurname().length() > 100) {
            throw new RuntimeException("Surname is too long (max 100 characters)");
        }
        if (doctor.getAddress() != null && doctor.getAddress().length() > 200) {
            throw new RuntimeException("Address is too long (max 200 characters)");
        }
        if (doctor.getPesel() != null && !doctor.getPesel().isEmpty()) {
            if (doctor.getPesel().length() != 11) {
                throw new RuntimeException("PESEL must be exactly 11 characters");
            }
            if (!doctor.getPesel().matches("\\d+")) {
                throw new RuntimeException("PESEL must contain only digits");
            }
        }
        
        // Check for duplicate PESEL
        if (doctor.getPesel() != null && !doctor.getPesel().isEmpty()) {
            Optional<Doctor> existing = doctorRepository.findByPesel(doctor.getPesel());
            if (existing.isPresent() && !existing.get().getId().equals(doctor.getId())) {
                throw new RuntimeException("Doctor with this PESEL already exists");
            }
        }
        
        return doctorRepository.save(doctor);
    }

    /**
     * Returns a list of doctors mapped as DoctorDTO objects
     * (to not give out personal information like PESEL)
     */
    public List<DoctorDTO> getDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorDTO::new)
                .toList();
    }

    /**
     * Returns an optional Doctor object based on the given id
     * @param id id of the doctor
     */
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow();
    }

    /**
     * Deletes the doctor from the database with specific id if he exists
     * @param id id of a doctor that's to be deleted
     * @throws Exception if doctor doesn't exist or has assigned shifts
     */
    public void deleteDoctorById(Long id) throws Exception {
        if (!doctorRepository.existsById(id)) {
            throw new Exception("Doctor does not exist.");
        }
        Doctor doctor = doctorRepository.findById(id).orElseThrow();
        if (!doctor.getShifts().isEmpty()) {
            throw new Exception("Cannot delete doctor with assigned shifts. Please delete shifts first.");
        }
        doctorRepository.deleteById(id);
    }

    /**
     * @param id id of the doctor
     * @return List of shifts assigned to a doctor
     */
    public List<ShiftDoctorResponseDTO> getShifts(Long id) {
        return getDoctorById(id).getShifts()
                .stream()
                .map(ShiftDoctorResponseDTO::new)
                .toList();
    }
}
