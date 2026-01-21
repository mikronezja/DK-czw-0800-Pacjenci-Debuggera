package com.oot.clinic.repositories;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.enumeration.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByPesel(String pesel);
    List<Doctor> findBySpecialization(Specialization specialization);
}