package com.oot.clinic.controllers;

import com.oot.clinic.DTOs.shift.ShiftRequestDTO;
import com.oot.clinic.DTOs.shift.ShiftResponseDTO;
import com.oot.clinic.exceptions.ConflictException;
import com.oot.clinic.exceptions.ResourceNotFoundException;
import com.oot.clinic.exceptions.ValidationException;
import com.oot.clinic.services.ShiftService;
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
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @Operation(summary = "Get all shifts", description = "Returns a list of all shifts in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Shifts list accessed successfully",
                    content = @Content(schema = @Schema(implementation = ShiftResponseDTO.class)))
    })
    @GetMapping
    public List<ShiftResponseDTO> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @Operation(summary = "Create a shift", description = "Assign a shift to a specific doctor in an office")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Shift created successfully",
                    content = @Content(schema = @Schema(implementation =  ShiftResponseDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data")
    })

    @PostMapping("/add")
    public ResponseEntity<?> createShift(@RequestBody ShiftRequestDTO shiftRequest) {
        try {
            ShiftResponseDTO shift = shiftService.createShift(
                    shiftRequest.getDoctorId(),
                    shiftRequest.getOfficeId(),
                    shiftRequest.getDayOfWeek(),
                    shiftRequest.getStartTime(),
                    shiftRequest.getEndTime()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(shift);

        } catch (ValidationException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (ResourceNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());

        } catch (ConflictException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @Operation(summary = "Delete a shift", description = "Delete a shift from the system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Shift deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Shift not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        try {
            shiftService.deleteShiftById(id);
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Edit existing shift", description = "Allows to make any changes to an existing shift")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Shift edited successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Shift not found",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShift(
            @PathVariable Long id,
            @RequestBody ShiftRequestDTO shiftRequest
    ) {
        try {
            ShiftResponseDTO updated = new ShiftResponseDTO(
                    shiftService.editShift(
                            id,
                            shiftRequest.getDoctorId(),
                            shiftRequest.getOfficeId(),
                            shiftRequest.getDayOfWeek(),
                            shiftRequest.getStartTime(),
                            shiftRequest.getEndTime()
                    )
            );

            return ResponseEntity.ok(updated);

        } catch (ValidationException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (ResourceNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());

        } catch (ConflictException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }
}
