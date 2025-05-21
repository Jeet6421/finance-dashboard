package com.financedash.finance_dashboard.controller;

import com.financedash.finance_dashboard.payload.ExpenseDTO;
import com.financedash.finance_dashboard.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.financedash.finance_dashboard.config.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiEndpoints.FinancePaths.EXPENSES)
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "Add new expense entry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid expense data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        return ResponseEntity.ok(expenseService.addExpense(expenseDTO));
    }

    @Operation(summary = "Get all expense entries")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retrieved all expense entries"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }
}