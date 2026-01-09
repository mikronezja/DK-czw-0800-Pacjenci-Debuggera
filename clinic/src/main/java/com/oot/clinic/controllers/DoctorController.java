package com.oot.clinic.controllers;

import com.oot.clinic.DTOs.doctor.DoctorRequestDTO;
import com.oot.clinic.DTOs.doctor.DoctorResponseDTO;
import com.oot.clinic.DTOs.shift.ShiftDoctorResponseDTO;
import com.oot.clinic.DTOs.doctor.DoctorDTO;
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
                    content = @Content(schema = @Schema(implementation =  DoctorResponseDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(@RequestBody DoctorRequestDTO doctor) {
        try {
            DoctorResponseDTO addedDoctor = doctorService.addDoctor(
                    doctor.getName(),
                    doctor.getSurname(),
                    doctor.getPesel(),
                    doctor.getAddress(),
                    doctor.getSpecialization()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(addedDoctor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating doctor");
        }
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
                    content = @Content(schema = @Schema(implementation =  DoctorDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new DoctorDTO(doctorService.getDoctorById(id)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctorById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("shifts")) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Get doctor's shifts", description = "Returns a list of all shifts assigned to a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Shifts list accessed successfully",
                    content = @Content(schema = @Schema(implementation =  ShiftDoctorResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}/shifts")
    public ResponseEntity<List<ShiftDoctorResponseDTO>> getDoctorShifts(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(doctorService.getShifts(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

