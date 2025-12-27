package com.oot.clinic.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Office {

    @Id
    @GeneratedValue
    private Long id;
    private int roomNumber;
    @OneToMany(mappedBy = "office")
    private List<Shift> shifts = new ArrayList<>();


    // GETTERS AND SETTERS

    public Long getId() {
        return id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public List<Shift>  getShifts() {
        return shifts;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
