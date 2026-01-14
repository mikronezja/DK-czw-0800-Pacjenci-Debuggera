package com.oot.clinic.services;

import com.oot.clinic.DTOs.office.OfficeResponseDTO;
import com.oot.clinic.DTOs.shift.ShiftOfficeResponseDTO;
import com.oot.clinic.entities.Office;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.repositories.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    public OfficeResponseDTO addOffice(int roomNumber) {

        if (roomNumber <= 0) {
            throw new ValidationException("Numer gabinetu musi być większy od 0");
        }

        if (officeRepository.findByRoomNumber(roomNumber).isPresent()) {
            throw new ConflictException("Gabinet o tym numerze już istnieje");
        }

        Office office = new Office(roomNumber);
        officeRepository.save(office);

        return new OfficeResponseDTO(office);
    }

    public List<OfficeResponseDTO> getOffices() {
        return officeRepository.findAll()
                .stream()
                .map(OfficeResponseDTO::new)
                .toList();
    }

    public void deleteOfficeById(Long id) {

        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gabinet", id));

        if (!office.getShifts().isEmpty()) {
            throw new ConflictException("Nie można usunąć gabinetu, ponieważ ma przypisane dyżury");
        }

        officeRepository.delete(office);
    }

    public List<ShiftOfficeResponseDTO> getShifts(Long id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gabinet", id));

        return office.getShifts()
                .stream()
                .map(ShiftOfficeResponseDTO::new)
                .toList();
    }
}
