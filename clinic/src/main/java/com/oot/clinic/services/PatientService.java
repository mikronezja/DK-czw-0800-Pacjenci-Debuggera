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
     */
    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Returns a list of patients mapped as PatientDTO objects
     * (to not give out personal information like PESEL or address)
     */
    public List<PatientDTO> getDoctors() {
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
