package com.oot.clinic.controllers;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.DTOs.DoctorDTO;
import com.oot.clinic.services.DoctorService;
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
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Swagger annotations
    @Operation(summary = "Create a new doctor", description = "Add a new doctor to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Doctor created successfully",
                    content = @Content(schema = @Schema(implementation =  Doctor.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return doctorService.addDoctor(doctor);
    }


    @Operation(summary = "Get all doctors", description = "Shows all doctors basic information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Doctor list accessed successfully",
                    content = @Content(schema = @Schema(implementation =  DoctorDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public List<DoctorDTO> getDoctors() {
        return doctorService.getDoctors();
    }


    @Operation(summary = "Get doctor details", description = "Access doctor's details by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Doctor details accessed successfully",
                    content = @Content(schema = @Schema(implementation =  Doctor.class))),
            @ApiResponse(responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Delete a doctor", description = "Delete a doctor from the system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctorById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

