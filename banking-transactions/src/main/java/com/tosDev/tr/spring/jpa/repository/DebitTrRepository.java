package com.tosDev.tr.spring.jpa.repository;

import com.tosDev.tr.spring.jpa.entity.DebitTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitTrRepository extends JpaRepository<DebitTransaction,Long> {
    List<DebitTransaction> findAllByLimitExceededTrue();
}
