package com.oot.clinic.services;

import com.oot.clinic.DTOs.PatientDTO;
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
     * @param patient the Patient object that's being added
     * @throws RuntimeException if PESEL already exists or fields are too long
     */
    public Patient addPatient(Patient patient) {
        // Validate required fields
        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (patient.getSurname() == null || patient.getSurname().trim().isEmpty()) {
            throw new RuntimeException("Surname is required");
        }
        
        // Trim whitespace
        patient.setName(patient.getName().trim());
        patient.setSurname(patient.getSurname().trim());
        if (patient.getAddress() != null) patient.setAddress(patient.getAddress().trim());
        if (patient.getPesel() != null) patient.setPesel(patient.getPesel().trim());
        
        // Validate field lengths
        if (patient.getName().length() > 100) {
            throw new RuntimeException("Name is too long (max 100 characters)");
        }
        if (patient.getSurname().length() > 100) {
            throw new RuntimeException("Surname is too long (max 100 characters)");
        }
        if (patient.getAddress() != null && patient.getAddress().length() > 200) {
            throw new RuntimeException("Address is too long (max 200 characters)");
        }
        if (patient.getPesel() != null && !patient.getPesel().isEmpty()) {
            if (patient.getPesel().length() != 11) {
                throw new RuntimeException("PESEL must be exactly 11 characters");
            }
            if (!patient.getPesel().matches("\\d+")) {
                throw new RuntimeException("PESEL must contain only digits");
            }
        }
        
        // Check for duplicate PESEL
        if (patient.getPesel() != null && !patient.getPesel().isEmpty()) {
            Optional<Patient> existing = patientRepository.findByPesel(patient.getPesel());
            if (existing.isPresent() && !existing.get().getId().equals(patient.getId())) {
                throw new RuntimeException("Patient with this PESEL already exists");
            }
        }
        
        return patientRepository.save(patient);
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
