package com.financedash.finance_dashboard.repository;

import com.financedash.finance_dashboard.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    @Query("SELECT SUM(i.amount) FROM Investment i WHERE MONTH(i.investmentDate) = MONTH(CURRENT_DATE) AND YEAR(i.investmentDate) = YEAR(CURRENT_DATE)")
    Double getMonthlyInvestment();
}
