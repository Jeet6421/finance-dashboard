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
@Table(name = "investment")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "amount", nullable = false)
    private double amount;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "returnRate")
    private double returnRate;

    @Column(nullable = false)
    private LocalDate investmentDate;

    @Column(name = "description")
    private String description;
}
