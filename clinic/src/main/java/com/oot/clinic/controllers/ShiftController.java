package com.oot.clinic.controllers;

import com.oot.clinic.DTOs.ShiftRequest;
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

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Shift createShift(@RequestBody ShiftRequest shiftRequest){
         return shiftService.createShift(
                shiftRequest.getDoctorId(),
                shiftRequest.getOfficeId(),
                shiftRequest.getDayOfWeek(),
                shiftRequest.getStartTime(),
                shiftRequest.getEndTime()
        );
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
}
