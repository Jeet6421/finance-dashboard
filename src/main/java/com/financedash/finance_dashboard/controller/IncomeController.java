package com.financedash.finance_dashboard.controller;

import com.financedash.finance_dashboard.config.ApiEndpoints;
import com.financedash.finance_dashboard.payload.IncomeDTO;
import com.financedash.finance_dashboard.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.FinancePaths.INCOME)
@RequiredArgsConstructor
@Tag(name = "Income", description = "Income management endpoints")
public class IncomeController {
    
    private final IncomeService incomeService;

    @Operation(summary = "Add new income entry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Income entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid income data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@Valid @RequestBody IncomeDTO incomeDTO) {
        return ResponseEntity.ok(incomeService.addIncome(incomeDTO));
    }

    @Operation(summary = "Get all income entries")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retrieved all income entries"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getAllIncome() {
        return ResponseEntity.ok(incomeService.getAllIncome());
    }
}