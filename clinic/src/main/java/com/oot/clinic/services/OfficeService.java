package com.oot.clinic.services;

import com.oot.clinic.DTOs.office.OfficeResponseDTO;
import com.oot.clinic.DTOs.shift.ShiftOfficeResponseDTO;
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
     * @param roomNumber
     * @return OfficeResponseDTO
     * @throws RuntimeException if office with number already exists or number is invalid
     */
    public OfficeResponseDTO addOffice(int roomNumber) {
        // Validate room number
        if (roomNumber <= 0) {
            throw new RuntimeException("Room number must be greater than 0");
        }

        // Check for duplicate room number
        Optional<Office> existing = officeRepository.findByRoomNumber(roomNumber);
        if (existing.isPresent()) { // && !existing.get().getId().equals(office.getId()) again
            throw new RuntimeException("Office with this room number already exists");
        }

        Office office = new Office(roomNumber);
        officeRepository.save(office);
        return new OfficeResponseDTO(office);
    }

    /**
     * Returns a list of existing offices
     */
    public List<OfficeResponseDTO> getOffices() {
        return officeRepository.findAll()
                .stream()
                .map(OfficeResponseDTO::new)
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
