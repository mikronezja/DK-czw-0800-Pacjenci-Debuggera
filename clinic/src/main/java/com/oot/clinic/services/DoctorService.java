package com.oot.clinic.services;

import com.oot.clinic.DTOs.appointment.AppointmentDoctorResponseDTO;
import com.oot.clinic.DTOs.doctor.DoctorResponseDTO;
import com.oot.clinic.DTOs.shift.ShiftDoctorResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.DTOs.doctor.DoctorDTO;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.repositories.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponseDTO addDoctor(
            String name,
            String surname,
            String pesel,
            String address,
            Specialization specialization
    ) {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Imię jest wymagane");
        }

        if (surname == null || surname.trim().isEmpty()) {
            throw new ValidationException("Nazwisko jest wymagane");
        }

        if (specialization == null) {
            throw new ValidationException("Specjalizacja jest wymagana");
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

            if (doctorRepository.findByPesel(pesel).isPresent()) {
                throw new ConflictException("Lekarz z tym numerem PESEL już istnieje");
            }
        }

        Doctor doctor = new Doctor(name, surname, pesel, specialization, address);
        doctorRepository.save(doctor);

        return new DoctorResponseDTO(doctor);
    }

    public List<DoctorDTO> getDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorDTO::new)
                .toList();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", id));
    }

    public void deleteDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", id));

        if (!doctor.getShifts().isEmpty()) {
            throw new ConflictException("Nie można usunąć lekarza, ponieważ ma przypisane dyżury");
        }

        doctorRepository.delete(doctor);
    }

    public List<ShiftDoctorResponseDTO> getShifts(Long id) {
        return getDoctorById(id).getShifts()
                .stream()
                .map(ShiftDoctorResponseDTO::new)
                .toList();
    }

    public List<AppointmentDoctorResponseDTO> getAppointments(Long id){
        return getDoctorById(id).getAppointments()
                .stream()
                .map(AppointmentDoctorResponseDTO::new)
                .toList();
    }
}
