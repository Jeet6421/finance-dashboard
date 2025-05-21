package com.financedash.finance_dashboard.service;


import com.financedash.finance_dashboard.payload.IncomeDTO;

import java.util.List;

public interface IncomeService {

    IncomeDTO addIncome(IncomeDTO income);

    List<IncomeDTO> getAllIncome();
}
