package com.financedash.finance_dashboard.controller;

import com.financedash.finance_dashboard.payload.InvestmentDTO;
import com.financedash.finance_dashboard.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.financedash.finance_dashboard.config.ApiEndpoints;

@RestController
@RequestMapping(ApiEndpoints.FinancePaths.INVESTMENTS)
@RequiredArgsConstructor
@Tag(name = "Investments", description = "Investment management endpoints")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Operation(summary = "Add new investment entry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Investment entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid investment data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping
    public ResponseEntity<InvestmentDTO> addInvestment(@Valid @RequestBody InvestmentDTO investmentDTO) {
        return ResponseEntity.ok(investmentService.addInvestment(investmentDTO));
    }

    @Operation(summary = "Get all investment entries")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retrieved all investment entries"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping
    public ResponseEntity<List<InvestmentDTO>> getAllInvestments() {
        return ResponseEntity.ok(investmentService.getAllInvestments());
    }

    @Operation(summary = "Get investment by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Investment found"),
        @ApiResponse(responseCode = "404", description = "Investment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvestmentDTO> getInvestmentById(@PathVariable Long id) {
        return ResponseEntity.ok(investmentService.getInvestmentById(id));
    }
}