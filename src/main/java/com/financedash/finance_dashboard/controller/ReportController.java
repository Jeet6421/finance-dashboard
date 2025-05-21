package com.financedash.finance_dashboard.controller;

import com.financedash.finance_dashboard.config.ApiEndpoints;
import com.financedash.finance_dashboard.payload.ReportDTO;
import com.financedash.finance_dashboard.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(ApiEndpoints.AuthPaths.ReportPaths.BASE)
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Financial reports API endpoints")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get monthly financial report")
    @GetMapping(ApiEndpoints.AuthPaths.ReportPaths.MONTHLY)
    public ResponseEntity<ReportDTO> getMonthlyReport() {
        return ResponseEntity.ok(reportService.getMonthlyReport());
    }

    @Operation(summary = "Get category-wise financial report")
    @GetMapping(ApiEndpoints.AuthPaths.ReportPaths.CATEGORY)
    public ResponseEntity<ReportDTO> getReportByCategory() {
        return ResponseEntity.ok(ReportDTO.builder().build());
    }
}