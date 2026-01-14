package com.oot.clinic.services;

import com.oot.clinic.DTOs.shift.ShiftResponseDTO;
import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
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

    public ShiftService(ShiftRepository shiftRepository,
                        DoctorRepository doctorRepository,
                        OfficeRepository officeRepository) {
        this.shiftRepository = shiftRepository;
        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
    }

    public List<ShiftResponseDTO> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(ShiftResponseDTO::new)
                .toList();
    }

    public ShiftResponseDTO createShift(
            Long doctorId,
            Long officeId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    ) {

        if (doctorId == null) {
            throw new ValidationException("Identyfikator lekarza jest wymagany");
        }
        if (officeId == null) {
            throw new ValidationException("Identyfikator gabinetu jest wymagany");
        }
        if (dayOfWeek == null) {
            throw new ValidationException("Dzień tygodnia jest wymagany");
        }
        if (startTime == null) {
            throw new ValidationException("Godzina rozpoczęcia jest wymagana");
        }
        if (endTime == null) {
            throw new ValidationException("Godzina zakończenia jest wymagana");
        }

        if (!startTime.isBefore(endTime)) {
            throw new ValidationException("Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));

        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResourceNotFoundException("Gabinet", officeId));

        // Konflikt – lekarz zajęty
        List<Shift> existingDoctorShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getDoctor().getId().equals(doctorId)
                        && s.getDayOfWeek().equals(dayOfWeek))
                .toList();

        for (Shift existingShift : existingDoctorShifts) {
            if (isTimeOverlapping(startTime, endTime,
                    existingShift.getStartTime(), existingShift.getEndTime())) {

                throw new ConflictException(
                        "Lekarz ma już dyżur w tym czasie w " + dayOfWeek +
                                " (istniejący: " + existingShift.getStartTime() + "-" + existingShift.getEndTime() +
                                ", nowy: " + startTime + "-" + endTime + ")"
                );
            }
        }

        // Konflikt – gabinet zajęty
        List<Shift> existingOfficeShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getOffice().getId().equals(officeId)
                        && s.getDayOfWeek().equals(dayOfWeek))
                .toList();

        for (Shift existingShift : existingOfficeShifts) {
            if (isTimeOverlapping(startTime, endTime,
                    existingShift.getStartTime(), existingShift.getEndTime())) {

                throw new ConflictException(
                        "Gabinet jest już zajęty w tym czasie w " + dayOfWeek +
                                " (istniejący: " + existingShift.getStartTime() + "-" + existingShift.getEndTime() +
                                ", nowy: " + startTime + "-" + endTime + ")"
                );
            }
        }

        Shift shift = shiftRepository.save(
                new Shift(doctor, office, dayOfWeek, startTime, endTime)
        );

        return new ShiftResponseDTO(shift);
    }

    private boolean isTimeOverlapping(LocalTime start1, LocalTime end1,
                                      LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public void deleteShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dyżur", id));

        shiftRepository.delete(shift);
    }

    public Shift editShift(
            Long id,
            Long doctorId,
            Long officeId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    ) {

        if (id == null) {
            throw new ValidationException("Identyfikator dyżuru jest wymagany");
        }
        if (doctorId == null) {
            throw new ValidationException("Identyfikator lekarza jest wymagany");
        }
        if (officeId == null) {
            throw new ValidationException("Identyfikator gabinetu jest wymagany");
        }
        if (dayOfWeek == null) {
            throw new ValidationException("Dzień tygodnia jest wymagany");
        }
        if (startTime == null) {
            throw new ValidationException("Godzina rozpoczęcia jest wymagana");
        }
        if (endTime == null) {
            throw new ValidationException("Godzina zakończenia jest wymagana");
        }

        if (!startTime.isBefore(endTime)) {
            throw new ValidationException("Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia");
        }

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dyżur", id));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lekarz", doctorId));

        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResourceNotFoundException("Gabinet", officeId));

        // Konflikt – lekarz zajęty
        List<Shift> existingDoctorShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getDoctor().getId().equals(doctorId)
                        && s.getDayOfWeek().equals(dayOfWeek)
                        && !s.getId().equals(id))
                .toList();

        for (Shift existingShift : existingDoctorShifts) {
            if (isTimeOverlapping(startTime, endTime,
                    existingShift.getStartTime(), existingShift.getEndTime())) {

                throw new ConflictException(
                        "Lekarz ma już dyżur w tym czasie w " + dayOfWeek +
                                " (istniejący: " + existingShift.getStartTime() + "-" + existingShift.getEndTime() +
                                ", nowy: " + startTime + "-" + endTime + ")"
                );
            }
        }

        // Konflikt – gabinet zajęty
        List<Shift> existingOfficeShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getOffice().getId().equals(officeId)
                        && s.getDayOfWeek().equals(dayOfWeek)
                        && !s.getId().equals(id))
                .toList();

        for (Shift existingShift : existingOfficeShifts) {
            if (isTimeOverlapping(startTime, endTime,
                    existingShift.getStartTime(), existingShift.getEndTime())) {

                throw new ConflictException(
                        "Gabinet jest już zajęty w tym czasie w " + dayOfWeek +
                                " (istniejący: " + existingShift.getStartTime() + "-" + existingShift.getEndTime() +
                                ", nowy: " + startTime + "-" + endTime + ")"
                );
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