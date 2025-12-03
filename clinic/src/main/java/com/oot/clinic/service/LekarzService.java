package com.oot.clinic.service;

import com.oot.clinic.model.Lekarz;
import com.oot.clinic.repository.LekarzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LekarzService {
    
    private final LekarzRepository lekarzRepository;
    
    @Autowired
    public LekarzService(LekarzRepository lekarzRepository) {
        this.lekarzRepository = lekarzRepository;
    }
    
    public List<Lekarz> getAllLekarze() {
        return lekarzRepository.findAll();
    }
    
    public Optional<Lekarz> getLekarzById(Long id) {
        return lekarzRepository.findById(id);
    }
    
    public Lekarz saveLekarz(Lekarz lekarz) {
        return lekarzRepository.save(lekarz);
    }
    
    public void deleteLekarz(Long id) {
        lekarzRepository.deleteById(id);
    }
}

