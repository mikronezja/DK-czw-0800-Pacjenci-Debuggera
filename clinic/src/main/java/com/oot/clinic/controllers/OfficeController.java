package com.oot.clinic.controllers;

import com.oot.clinic.entities.Office;
import com.oot.clinic.services.OfficeService;
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
@RequestMapping("/offices")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @Operation(summary = "Create a new office", description = "Add a new office to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Office created successfully",
                    content = @Content(schema = @Schema(implementation =  Office.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Office addOffice(@RequestBody Office office) {
        return officeService.addOffice(office);
    }


    @Operation(summary = "See offices", description = "Shows all existing offices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Office list accessed successfully",
                    content = @Content(schema = @Schema(implementation =  Office.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public List<Office> getOffices() {
        return officeService.getOffices();
    }


    @Operation(summary = "Delete an office", description = "Delete an office from the system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Office deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Office not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        try {
            officeService.deleteOfficeById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
