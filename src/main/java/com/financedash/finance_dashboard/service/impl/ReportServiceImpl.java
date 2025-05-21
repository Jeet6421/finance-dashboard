package com.financedash.finance_dashboard.service.impl;

import com.financedash.finance_dashboard.payload.ReportDTO;
import com.financedash.finance_dashboard.repository.ExpenseRepository;
import com.financedash.finance_dashboard.repository.IncomeRepository;
import com.financedash.finance_dashboard.repository.InvestmentRepository;
import com.financedash.finance_dashboard.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;

    @Override
    public ReportDTO getMonthlyReport() {
        // Fetch data with null safety
        Double totalIncome = incomeRepository.getMonthlyIncome();
        Double totalExpense = expenseRepository.getMonthlyExpense();
        Double totalInvestment = investmentRepository.getMonthlyInvestment();

        totalIncome = totalIncome != null ? totalIncome : 0.0;
        totalExpense = totalExpense != null ? totalExpense : 0.0;
        totalInvestment = totalInvestment != null ? totalInvestment : 0.0;

        // Calculate net profit
        Double netProfit = totalIncome - totalExpense;

        log.info("Monthly Report - Income: {}, Expense: {}, Investment: {}, NetProfit: {}",
                totalIncome, totalExpense, totalInvestment, netProfit);

        return ReportDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalInvestment(totalInvestment)
                .netProfit(netProfit)
                .build();
    }
}
