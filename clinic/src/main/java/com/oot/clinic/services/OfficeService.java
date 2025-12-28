package com.oot.clinic.services;

import com.oot.clinic.DTOs.OfficeDTO;
import com.oot.clinic.DTOs.ShiftOfficeResponseDTO;
import com.oot.clinic.entities.Office;
import com.oot.clinic.repositories.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {this.officeRepository = officeRepository;}

    /**
     * Adds a new office to the database
     * @param office the Office object that's being added
     * @throws RuntimeException if room number already exists or is invalid
     */
    public Office addOffice(Office office) {
        // Validate room number
        if (office.getRoomNumber() <= 0) {
            throw new RuntimeException("Room number must be greater than 0");
        }
        
        // Check for duplicate room number
        Optional<Office> existing = officeRepository.findByRoomNumber(office.getRoomNumber());
        if (existing.isPresent() && !existing.get().getId().equals(office.getId())) {
            throw new RuntimeException("Office with this room number already exists");
        }
        
        return officeRepository.save(office);
    }

    /**
     * Returns a list of existing offices
     */
    public List<OfficeDTO> getOffices() {
        return officeRepository.findAll()
                .stream()
                .map(OfficeDTO::new)
                .toList();
    }

    /**
     * Deletes the office from the database with specific id if it exists
     * @param id id of the office that's to be deleted
     * @throws Exception if office doesn't exist or has assigned shifts
     */
    public void deleteOfficeById(Long id) throws Exception {
        if (!officeRepository.existsById(id)) {
            throw new Exception("Office does not exist.");
        }
        Office office = officeRepository.findById(id).orElseThrow();
        if (!office.getShifts().isEmpty()) {
            throw new Exception("Cannot delete office with assigned shifts. Please delete shifts first.");
        }
        officeRepository.deleteById(id);
    }

    /**
     * @param id of the office
     * @return a List of shifts assigned to this office
     */
    public List<ShiftOfficeResponseDTO> getShifts(Long id){
        return officeRepository.findById(id).orElseThrow().getShifts()
                .stream()
                .map(ShiftOfficeResponseDTO::new)
                .toList();
    }
}
