package com.financedash.finance_dashboard.service.impl;

import com.financedash.finance_dashboard.entity.Expense;
import com.financedash.finance_dashboard.payload.ExpenseDTO;
import com.financedash.finance_dashboard.repository.ExpenseRepository;
import com.financedash.finance_dashboard.service.ExpenseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        // Convert DTO to entity
        Expense expense = modelMapper.map(expenseDTO, Expense.class);
        Expense savedExpense = expenseRepository.save(expense);
        return modelMapper.map(savedExpense, ExpenseDTO.class);
    }

    // Get all expenses
    @Override
    public List<ExpenseDTO> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(expense -> modelMapper.map(expense, ExpenseDTO.class))
                .collect(Collectors.toList());
    }
}
