package com.financedash.finance_dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "amount", nullable = false)
    private double amount;
    @Column(name = "category", nullable = false)
    private String category;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "notes")
    private String notes;
}
