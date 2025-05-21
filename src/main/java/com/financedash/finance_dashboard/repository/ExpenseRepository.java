package com.financedash.finance_dashboard.repository;

import com.financedash.finance_dashboard.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE MONTH(e.date) = MONTH(CURRENT_DATE) AND YEAR(e.date) = YEAR(CURRENT_DATE)")
    Double getMonthlyExpense();
}
