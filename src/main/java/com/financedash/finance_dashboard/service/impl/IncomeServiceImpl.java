package com.financedash.finance_dashboard.service.impl;

import com.financedash.finance_dashboard.entity.Income;
import com.financedash.finance_dashboard.payload.IncomeDTO;
import com.financedash.finance_dashboard.repository.IncomeRepository;
import com.financedash.finance_dashboard.service.IncomeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Add income
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        // Convert DTO to entity
        Income income = modelMapper.map(incomeDTO, Income.class);
        Income savedIncome = incomeRepository.save(income);
        // Convert entity back to DTO for response
        return modelMapper.map(savedIncome, IncomeDTO.class);
    }

    // Get all incomes
    public List<IncomeDTO> getAllIncome() {
        List<Income> incomes = incomeRepository.findAll();
        return incomes.stream()
                .map(income -> modelMapper.map(income, IncomeDTO.class))
                .collect(Collectors.toList());
    }
}
