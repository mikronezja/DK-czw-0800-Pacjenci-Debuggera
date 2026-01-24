package com.oot.clinic.controllers;

import com.oot.clinic.DTOs.appointment.AppointmentAvailabilityRequestDTO;
import com.oot.clinic.DTOs.appointment.AppointmentRequestDTO;
import com.oot.clinic.DTOs.appointment.AppointmentResponseDTO;
import com.oot.clinic.exceptions.InavailabilityException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.services.AppointmentService;
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
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Create an appointment", description = "Make an appointment for a patient with a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Appointment created successfully",
                    content = @Content(schema = @Schema(implementation =  AppointmentResponseDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data")
    })
    @PostMapping("/add")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequestDTO appointmentRequest) {
        try {
            AppointmentResponseDTO appointment = appointmentService.createAppointment(
                    appointmentRequest.getDoctorId(),
                    appointmentRequest.getPatientId(),
                    appointmentRequest.getDate(),
                    appointmentRequest.getStartTime(),
                    appointmentRequest.getEndTime()
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(appointment);

        } catch (ValidationException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (ResourceNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());

        } catch (InavailabilityException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }


    @Operation(summary = "Get all appointments", description = "Returns a list of all appointments in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Appointment list accessed successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        try {
            return ResponseEntity.ok(appointmentService.getAllAppointments().stream().map(AppointmentResponseDTO::new).toList());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(summary = "Edit existing appointment", description = "Allows to make any changes to an existing appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Appointment edited successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequestDTO appointment){
        try {
            return ResponseEntity.ok(new AppointmentResponseDTO(appointmentService.updateAppointment(id,
                    appointment.getDoctorId(),
                    appointment.getPatientId(),
                    appointment.getDate(),
                    appointment.getStartTime(),
                    appointment.getEndTime())));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @Operation(summary = "Delete an appointment", description = "Delete an appointment from the system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id){
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Check available appointment times", description = "Access a list of doctors with their availability hours for specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "List accessed successfully")
    })
    @PostMapping("/availabilities")
    public ResponseEntity<?> getAvailableAppointments(@RequestBody AppointmentAvailabilityRequestDTO appointmentRequest){
        try{
            return ResponseEntity.ok(appointmentService.availableAppointments(
                    appointmentRequest.date(),
                    appointmentRequest.specialization()));
        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (InavailabilityException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }


}
