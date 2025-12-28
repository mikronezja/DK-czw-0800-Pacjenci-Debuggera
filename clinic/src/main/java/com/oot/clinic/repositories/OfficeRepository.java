package com.oot.clinic.repositories;

import com.oot.clinic.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    Optional<Office> findByRoomNumber(int roomNumber);
}
