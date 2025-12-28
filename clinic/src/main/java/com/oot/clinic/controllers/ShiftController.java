package com.oot.clinic.controllers;

import com.oot.clinic.DTOs.ShiftRequestDTO;
import com.oot.clinic.DTOs.ShiftResponseDTO;
import com.oot.clinic.entities.Shift;
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
                    content = @Content(schema = @Schema(implementation =  Shift.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data")
    })
    @PostMapping("/add")
    public ResponseEntity<?> createShift(@RequestBody ShiftRequestDTO shiftRequest){
        try {
            ShiftResponseDTO shift = shiftService.createShift(
                    shiftRequest.getDoctorId(),
                    shiftRequest.getOfficeId(),
                    shiftRequest.getDayOfWeek(),
                    shiftRequest.getStartTime(),
                    shiftRequest.getEndTime()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(shift);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
        } catch (Exception e) {
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
    public ResponseEntity<?> updateShift(@PathVariable Long id, @RequestBody ShiftRequestDTO shiftRequest) {
        try {
            Shift updated = shiftService.editShift(id,
                    shiftRequest.getDoctorId(),
                    shiftRequest.getOfficeId(),
                    shiftRequest.getDayOfWeek(),
                    shiftRequest.getStartTime(),
                    shiftRequest.getEndTime());

            return ResponseEntity.ok(new ShiftResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
