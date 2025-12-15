package com.oot.clinic.services;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.OfficeRepository;
import com.oot.clinic.repositories.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;

    public ShiftService(ShiftRepository shiftRepository, DoctorRepository doctorRepository, OfficeRepository officeRepository) {
        this.shiftRepository = shiftRepository;
        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
    }

    /**
     *
     * @param doctorId
     * @param officeId
     * @param dayOfWeek
     * @param startTime
     * @param endTime
     * @return A successfully saved shift
     * @throws RuntimeException if there is no Doctor or no Office by their id
     */
    public Shift createShift(Long doctorId, Long officeId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Doctor doctor =  doctorRepository.findById(doctorId).orElseThrow(() ->  new RuntimeException("Doctor does not exist."));
        Office office = officeRepository.findById(officeId).orElseThrow(() -> new RuntimeException("Office does not exist."));
        return shiftRepository.save(new Shift(doctor, office, dayOfWeek, startTime, endTime));
    }

    /**
     * Deletes a shift from the database with specific id if it exists
     * @param id
     */
    public void deleteShiftById(Long id) throws Exception {
        if (!shiftRepository.existsById(id)) {
            throw new Exception("Doctor does not exist.");
        }
        shiftRepository.deleteById(id);
    }



}
