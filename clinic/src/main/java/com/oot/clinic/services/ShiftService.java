package com.oot.clinic.services;

import com.oot.clinic.DTOs.ShiftResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.OfficeRepository;
import com.oot.clinic.repositories.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

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
     * Returns a list of all shifts mapped as ShiftResponseDTO objects
     */
    public List<ShiftResponseDTO> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(ShiftResponseDTO::new)
                .toList();
    }

    /**
     *
     * @param doctorId
     * @param officeId
     * @param dayOfWeek
     * @param startTime
     * @param endTime
     * @return A successfully saved shift
     * @throws RuntimeException if there is no Doctor or no Office by their id, or if startTime >= endTime
     */
    public ShiftResponseDTO createShift(Long doctorId, Long officeId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RuntimeException("Start time must be before end time.");
        }
        
        Doctor doctor =  doctorRepository.findById(doctorId).orElseThrow(() ->  new RuntimeException("Doctor does not exist."));
        Office office = officeRepository.findById(officeId).orElseThrow(() -> new RuntimeException("Office does not exist."));
        
        // Check for overlapping shifts
        List<Shift> existingDoctorShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getDoctor().getId().equals(doctorId) && s.getDayOfWeek().equals(dayOfWeek))
                .toList();
        
        for (Shift existingShift : existingDoctorShifts) {
            if (isTimeOverlapping(startTime, endTime, existingShift.getStartTime(), existingShift.getEndTime())) {
                throw new RuntimeException("Doctor already has a shift at this time on " + dayOfWeek);
            }
        }
        
        List<Shift> existingOfficeShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getOffice().getId().equals(officeId) && s.getDayOfWeek().equals(dayOfWeek))
                .toList();
        
        for (Shift existingShift : existingOfficeShifts) {
            if (isTimeOverlapping(startTime, endTime, existingShift.getStartTime(), existingShift.getEndTime())) {
                throw new RuntimeException("Office already has a shift at this time on " + dayOfWeek);
            }
        }
        
        Shift shift = shiftRepository.save(new Shift(doctor, office, dayOfWeek, startTime, endTime));
        return new ShiftResponseDTO(shift);
    }
    
    private boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Deletes a shift from the database with specific id if it exists
     * @param id
     */
    public void deleteShiftById(Long id) throws Exception {
        if (!shiftRepository.existsById(id)) {
            throw new Exception("Shift does not exist.");
        }
        shiftRepository.deleteById(id);
    }

    /**
     *
     * @param id
     * @param doctorId
     * @param officeId
     * @param dayOfWeek
     * @param startTime
     * @param endTime
     * @return an edited shift
     * @throws Exception if Doctor/Shift/Office doesn't exist, or if startTime >= endTime
     */
    public Shift editShift(Long id, Long doctorId, Long officeId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) throws Exception {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RuntimeException("Start time must be before end time.");
        }
        
        Shift shift = shiftRepository.findById(id).orElseThrow(() ->  new RuntimeException("Shift does not exist."));
        Doctor doctor =  doctorRepository.findById(doctorId).orElseThrow(() ->  new RuntimeException("Doctor does not exist."));
        Office office = officeRepository.findById(officeId).orElseThrow(() -> new RuntimeException("Office does not exist."));

        // Check for overlapping shifts (excluding current shift)
        List<Shift> existingDoctorShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getDoctor().getId().equals(doctorId) 
                        && s.getDayOfWeek().equals(dayOfWeek)
                        && !s.getId().equals(id))
                .toList();
        
        for (Shift existingShift : existingDoctorShifts) {
            if (isTimeOverlapping(startTime, endTime, existingShift.getStartTime(), existingShift.getEndTime())) {
                throw new RuntimeException("Doctor already has a shift at this time on " + dayOfWeek);
            }
        }
        
        List<Shift> existingOfficeShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getOffice().getId().equals(officeId) 
                        && s.getDayOfWeek().equals(dayOfWeek)
                        && !s.getId().equals(id))
                .toList();
        
        for (Shift existingShift : existingOfficeShifts) {
            if (isTimeOverlapping(startTime, endTime, existingShift.getStartTime(), existingShift.getEndTime())) {
                throw new RuntimeException("Office already has a shift at this time on " + dayOfWeek);
            }
        }

        shift.setDoctor(doctor);
        shift.setOffice(office);
        shift.setDayOfWeek(dayOfWeek);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);

        return shiftRepository.save(shift);
    }



}
