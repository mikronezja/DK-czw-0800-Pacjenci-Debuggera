package com.oot.clinic.controller;

import com.oot.clinic.model.Lekarz;
import com.oot.clinic.service.LekarzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lekarze")
@CrossOrigin(origins = "http://localhost:5173")
public class LekarzController {
    
    private final LekarzService lekarzService;
    
    @Autowired
    public LekarzController(LekarzService lekarzService) {
        this.lekarzService = lekarzService;
    }
    
    @GetMapping
    public ResponseEntity<List<Lekarz>> getAllLekarze() {
        List<Lekarz> lekarze = lekarzService.getAllLekarze();
        return ResponseEntity.ok(lekarze);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Lekarz> getLekarzById(@PathVariable Long id) {
        return lekarzService.getLekarzById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Lekarz> createLekarz(@RequestBody Lekarz lekarz) {
        Lekarz savedLekarz = lekarzService.saveLekarz(lekarz);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLekarz);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLekarz(@PathVariable Long id) {
        if (lekarzService.getLekarzById(id).isPresent()) {
            lekarzService.deleteLekarz(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

