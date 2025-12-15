package com.oot.clinic.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Office {

    @Id
    @GeneratedValue
    private Long id;
    private int roomNumber;

    // GETTERS

    public Long getId() {
        return id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
}
