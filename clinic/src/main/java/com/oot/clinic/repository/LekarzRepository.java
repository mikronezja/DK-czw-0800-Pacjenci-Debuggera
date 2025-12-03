package com.oot.clinic.repository;

import com.oot.clinic.model.Lekarz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LekarzRepository extends JpaRepository<Lekarz, Long> {
}

