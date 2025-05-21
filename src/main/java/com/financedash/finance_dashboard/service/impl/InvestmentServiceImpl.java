package com.financedash.finance_dashboard.service.impl;

import com.financedash.finance_dashboard.entity.Investment;
import com.financedash.finance_dashboard.payload.InvestmentDTO;
import com.financedash.finance_dashboard.repository.InvestmentRepository;
import com.financedash.finance_dashboard.service.InvestmentService;
import com.financedash.finance_dashboard.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public  class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public InvestmentServiceImpl(InvestmentRepository investmentRepository, ModelMapper modelMapper) {
        this.investmentRepository = investmentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public InvestmentDTO addInvestment(InvestmentDTO investmentDTO) {
        Investment investment = modelMapper.map(investmentDTO, Investment.class);
        Investment saved = investmentRepository.save(investment);
        return modelMapper.map(saved, InvestmentDTO.class);
    }

    @Override
    public List<InvestmentDTO> getAllInvestments() {
        return investmentRepository.findAll()
                .stream()
                .map(investment -> modelMapper.map(investment, InvestmentDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public InvestmentDTO getInvestmentById(Long id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
        return modelMapper.map(investment, InvestmentDTO.class);
    }
}