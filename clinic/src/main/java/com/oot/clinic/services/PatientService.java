package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentPatientResponseDTO;
import com.oot.clinic.DTOs.patient.PatientDTO;
import com.oot.clinic.DTOs.patient.PatientResponseDTO;
import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponseDTO addPatient(
            String name,
            String surname,
            String pesel,
            String address
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Imię jest wymagane");
        }

        if (surname == null || surname.trim().isEmpty()) {
            throw new ValidationException("Nazwisko jest wymagane");
        }

        name = name.trim();
        surname = surname.trim();
        if (address != null) {
            address = address.trim();
        }
        if (pesel != null) {
            pesel = pesel.trim();
        }

        if (name.length() > 100) {
            throw new ValidationException("Imię jest za długie (maks. 100 znaków)");
        }

        if (surname.length() > 100) {
            throw new ValidationException("Nazwisko jest za długie (maks. 100 znaków)");
        }

        if (address != null && address.length() > 200) {
            throw new ValidationException("Adres jest za długi (maks. 200 znaków)");
        }

        if (pesel != null && !pesel.isEmpty()) {

            if (pesel.length() != 11) {
                throw new ValidationException("PESEL musi mieć dokładnie 11 cyfr");
            }

            if (!pesel.matches("\\d{11}")) {
                throw new ValidationException("PESEL może zawierać tylko cyfry");
            }

            if (patientRepository.findByPesel(pesel).isPresent()) {
                throw new ConflictException("Pacjent z tym numerem PESEL już istnieje");
            }
        }

        Patient patient = new Patient(name, surname, address, pesel);
        patientRepository.save(patient);

        return new PatientResponseDTO(patient);
    }

    public List<PatientDTO> getPatients() {
        return patientRepository.findAll().stream()
                .map(PatientDTO::new)
                .toList();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pacjent", id)
                );
    }

    public void deletePatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pacjent", id));

        patientRepository.delete(patient);
    }

    public List<AppointmentPatientResponseDTO> getAppointments(Long id){
        return getPatientById(id).getAppointments()
                .stream()
                .map(AppointmentPatientResponseDTO::new)
                .toList();
    }
}
