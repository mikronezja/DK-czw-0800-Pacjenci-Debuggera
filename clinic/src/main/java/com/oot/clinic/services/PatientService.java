package com.oot.clinic.services;

import com.oot.clinic.DTOs.patient.PatientDTO;
import com.oot.clinic.DTOs.patient.PatientResponseDTO;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Adds a new patient to the database
     * @param name
     * @param surname
     * @param pesel
     * @param address
     * @return PatientResponseDTO
     * @throws RuntimeException if pesel already exists or fields are too long
     */
    public PatientResponseDTO addPatient(String name, String surname, String pesel, String address) {
        // Validate required fields
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (surname == null || surname.trim().isEmpty()) {
            throw new RuntimeException("Surname is required");
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
            Optional<Patient> existing = patientRepository.findByPesel(pesel);
            if (existing.isPresent()) { //  && !existing.get().getId().equals(patient.getId()) again i think its skippable
                throw new RuntimeException("Patient with this PESEL already exists");
            }
        }

        Patient patient = new Patient(name, surname, address, pesel);
        patientRepository.save(patient);
        return new PatientResponseDTO(patient);
    }

    /**
     * Returns a list of patients mapped as PatientDTO objects
     * (to not give out personal information like PESEL or address)
     */
    public List<PatientDTO> getPatients() {
        return patientRepository.findAll().stream()
                .map(PatientDTO::new)
                .toList();
    }

    /**
     * Returns an optional Patient object based on the given id
     * @param id id of the patient
     */
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Deletes the patient from the database with specific id if he exists
     * @param id id of a patient that's to be deleted
     */
    public void deletePatientById(Long id) throws Exception {
        if (!patientRepository.existsById(id)) {
            throw new Exception("Patient does not exist.");
        }
        patientRepository.deleteById(id);
    }
}
