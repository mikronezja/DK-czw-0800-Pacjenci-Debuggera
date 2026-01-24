package com.oot.clinic.repositories;

import com.oot.clinic.entities.Appointment;
import com.oot.clinic.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List<Appointment> findByDoctorInAndDate(List<Doctor> doctors, LocalDate date);
}
