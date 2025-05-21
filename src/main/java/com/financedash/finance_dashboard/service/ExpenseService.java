package com.financedash.finance_dashboard.service;

import com.financedash.finance_dashboard.payload.ExpenseDTO;

import java.util.List;

public interface ExpenseService{

    ExpenseDTO addExpense(ExpenseDTO expense);

    List<ExpenseDTO> getAllExpenses();
}
