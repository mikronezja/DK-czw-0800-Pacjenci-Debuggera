package com.oot.clinic.services;

import com.oot.clinic.DTOs.OfficeDTO;
import com.oot.clinic.DTOs.ShiftOfficeResponseDTO;
import com.oot.clinic.entities.Office;
import com.oot.clinic.repositories.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {this.officeRepository = officeRepository;}

    /**
     * Adds a new office to the database
     * @param office the Office object that's being added
     */
    public Office addOffice(Office office) {
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
     */
    public void deleteOfficeById(Long id) throws Exception {
        if (!officeRepository.existsById(id)) {
            throw new Exception("Office does not exist.");
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
