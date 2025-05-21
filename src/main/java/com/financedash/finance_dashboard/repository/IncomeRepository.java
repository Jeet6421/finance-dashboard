package com.financedash.finance_dashboard.repository;

import com.financedash.finance_dashboard.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("SELECT SUM(i.amount) FROM Income i WHERE MONTH(i.date) = MONTH(CURRENT_DATE) AND YEAR(i.date) = YEAR(CURRENT_DATE)")
    Double getMonthlyIncome();
}
