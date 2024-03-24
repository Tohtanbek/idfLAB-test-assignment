package com.tosDev.spring.jpa.entity;

import com.tosDev.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
