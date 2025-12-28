package com.oot.clinic.controllers;

import com.oot.clinic.DTOs.PatientDTO;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    PatientController(PatientService patientService) {this.patientService = patientService;}

    @Operation(summary = "Create a new patient", description = "Add a new patient to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Patient created successfully",
                    content = @Content(schema = @Schema(implementation =  Patient.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/add")
    public ResponseEntity<?> addPatient(@RequestBody Patient patient) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(patientService.addPatient(patient));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating patient");
        }
    }


    @Operation(summary = "Get all patients", description = "Shows all patients basic information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Patients list accessed successfully",
                    content = @Content(schema = @Schema(implementation =  PatientDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public List<PatientDTO> getPatients() {
        return patientService.getPatients();
    }


    @Operation(summary = "Get patient details", description = "Access patient's details by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Patient details accessed successfully",
                    content = @Content(schema = @Schema(implementation =  Patient.class))),
            @ApiResponse(responseCode = "404",
                    description = "Patient not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Delete a patient", description = "Delete a patient from the system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Patient not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        try {
            patientService.deletePatientById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
