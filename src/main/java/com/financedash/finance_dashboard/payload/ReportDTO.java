package com.financedash.finance_dashboard.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double totalInvestment;
    private Double netProfit;
}
