package com.tosDev.tr.spring.jpa.entity;

import com.tosDev.tr.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class DebitTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountFrom;
    private Long accountTo;
    private Double sum;
    private char[] currencyCode;
    @Enumerated
    private ExpenseCategory expenseCategory;
    private LocalDateTime trDateTime;
    private Integer timeZoneSecOffset;
    private boolean limitExceeded;
}
