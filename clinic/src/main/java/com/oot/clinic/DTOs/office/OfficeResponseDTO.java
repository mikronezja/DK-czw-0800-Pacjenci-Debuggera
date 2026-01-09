package com.oot.clinic.DTOs.office;

import com.oot.clinic.entities.Office;

public class OfficeResponseDTO {

    private final Long id;
    private final int roomNumber;

    public OfficeResponseDTO(Office office){
        this.id = office.getId();
        this.roomNumber = office.getRoomNumber();
    }

    // GETTERS

    public Long getId() {
        return id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
}
