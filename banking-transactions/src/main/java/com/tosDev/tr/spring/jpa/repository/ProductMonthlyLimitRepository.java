package com.tosDev.tr.spring.jpa.repository;

import com.tosDev.tr.spring.jpa.entity.ProductMonthlyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductMonthlyLimitRepository extends JpaRepository<ProductMonthlyLimit,Integer> {
    @Transactional
    @Modifying
    @Query("update ProductMonthlyLimit l " +
            "SET l.limitLeft = l.limitLeft-:sum WHERE l.id=:id")
    void correctLimit(@Param("sum") Double sum,@Param("id") Integer limitId);

    @Transactional
    @Query("SELECT CASE WHEN l.limitLeft<0 THEN TRUE ELSE FALSE END " +
            "FROM ProductMonthlyLimit l " +
            "WHERE l.id=:id")
    boolean checkIfNegativeLimit(@Param("id") Integer id);

    @Transactional
    @Query(nativeQuery = true, value =
            "SELECT * FROM product_monthly_limit order by change_date_time DESC LIMIT 1")
    Optional<ProductMonthlyLimit> findByLastDateTime();

    /**
     * Метод ищет ближайший установленный лимит на транзакции по
     * категории продуктов до переданной даты транзакции
     * @param localDateTime дата транзакции по UTC
     * @return ближайший установленный лимит на категорию product
     */
    @Transactional
    @Query(nativeQuery = true, value =
            """
            SELECT * FROM product_monthly_limit 
            WHERE change_date_time < :date
            ORDER BY change_date_time DESC 
            LIMIT 1
            """)
    Optional<ProductMonthlyLimit> findByClosestDate(@Param("date") LocalDateTime localDateTime);
}
