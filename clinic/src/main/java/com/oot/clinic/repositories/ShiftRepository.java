package com.oot.clinic.repositories;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findShiftsByDoctorInAndDayOfWeek(List<Doctor> doctors, DayOfWeek dayOfWeek);
}
