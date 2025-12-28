package com.oot.clinic.DTOs;

import com.oot.clinic.entities.Office;

public class OfficeDTO {

    private final Long id;
    private final int roomNumber;

    public OfficeDTO(Office office){
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
