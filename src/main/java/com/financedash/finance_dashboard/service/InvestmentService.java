package com.financedash.finance_dashboard.service;

import com.financedash.finance_dashboard.payload.InvestmentDTO;

import java.util.List;

public interface InvestmentService {
    InvestmentDTO addInvestment(InvestmentDTO investmentDTO);
    List<InvestmentDTO> getAllInvestments();

    InvestmentDTO getInvestmentById(Long id);
}
