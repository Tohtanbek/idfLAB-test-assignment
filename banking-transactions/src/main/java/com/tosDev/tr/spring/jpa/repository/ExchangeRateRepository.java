package com.tosDev.tr.spring.jpa.repository;

import com.tosDev.tr.spring.jpa.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {
    Optional<ExchangeRate> findByCurrencyCode(char[] currencyCode);
}
