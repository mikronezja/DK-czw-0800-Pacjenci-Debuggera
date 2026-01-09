package com.oot.clinic.services;

import com.oot.clinic.DTOs.doctor.DoctorResponseDTO;
import com.oot.clinic.DTOs.shift.ShiftDoctorResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.DTOs.doctor.DoctorDTO;
import com.oot.clinic.entities.enumeration.Specialization;
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
     * @param name
     * @param surname
     * @param pesel
     * @param address
     * @param specialization
     * @return DoctorResponseDTO
     * @throws RuntimeException if a pesel already exists or fields are too long
     */
    public DoctorResponseDTO addDoctor(String name, String surname, String pesel, String address, Specialization specialization) {
        // Validate required fields
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (surname == null || surname.trim().isEmpty()) {
            throw new RuntimeException("Surname is required");
        }
        if (specialization == null) {
            throw new RuntimeException("Specialization is required");
        }

        // Trim whitespace
        name = name.trim();
        surname = surname.trim();
        if (address != null) address = address.trim();
        if (pesel != null) pesel = pesel.trim();

        // Validate field lengths
        if (name.length() > 100) {
            throw new RuntimeException("Name is too long (max 100 characters)");
        }
        if (surname.length() > 100) {
            throw new RuntimeException("Surname is too long (max 100 characters)");
        }
        if (address != null && address.length() > 200) {
            throw new RuntimeException("Address is too long (max 200 characters)");
        }
        if (pesel != null && !pesel.isEmpty()) {
            if (pesel.length() != 11) {
                throw new RuntimeException("PESEL must be exactly 11 characters");
            }
            if (!pesel.matches("\\d+")) {
                throw new RuntimeException("PESEL must contain only digits");
            }
        }

        // Check for duplicate PESEL
        if (pesel != null && !pesel.isEmpty()) {
            Optional<Doctor> existing = doctorRepository.findByPesel(pesel);
            if (existing.isPresent()) { // && !existing.get().getId().equals(doctor.getId()) raczej się nigdy nie wydarzy
                throw new RuntimeException("Doctor with this PESEL already exists");
            }
        }

        Doctor doctor = new Doctor(name, surname, pesel, specialization, address);
        doctorRepository.save(doctor);
        return new DoctorResponseDTO(doctor);
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
