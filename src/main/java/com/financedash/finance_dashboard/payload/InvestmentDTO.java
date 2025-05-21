package com.financedash.finance_dashboard.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Investment type is required")
    private String type;

    @NotNull(message = "Returns is required")
    @Positive(message = "Returns must be positive")
    private Double returns;

    @NotNull(message = "Investment date is required")
    private LocalDate investmentDate;

    private String description;
}
