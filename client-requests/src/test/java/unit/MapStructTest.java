package unit;

import com.tosDev.dto.ExceedTransactionDto;
import com.tosDev.enums.ExpenseCategory;
import com.tosDev.spring.config.BeanConfig;
import com.tosDev.spring.jpa.entity.DebitTransaction;
import com.tosDev.spring.jpa.entity.ProductMonthlyLimit;
import com.tosDev.spring.jpa.entity.ServiceMonthlyLimit;
import com.tosDev.util.mapstruct.MapStructMapperClient;
import com.tosDev.util.mapstruct.MapStructMapperClientImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;

@SpringBootTest(classes = {MapStructMapperClientImpl.class, BeanConfig.class})
public class MapStructTest {
    @Autowired
    private MapStructMapperClient mapper;

    LocalDateTime transactionDT;
    DebitTransaction debitTrDao;
    LocalDateTime limitDT;

    @BeforeEach
    void config() {
        transactionDT =
                LocalDateTime.of(2022,1,30,
                        6,0,0,0);
        debitTrDao = DebitTransaction
                .builder()
                .id(1L)
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyCode(new char[]{'U', 'S', 'D'})
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(transactionDT)
                .timeZoneSecOffset(21600)
                .limitExceeded(false)
                .build();

        limitDT =
                LocalDateTime.of(2022,1,25,
                        6,0,0,0);
    }

    @Test
    void checkTransactionAndProductLimitToDtoMapping(){

        ProductMonthlyLimit productLimitDao = ProductMonthlyLimit
                .builder()
                .currentLimit(1000.00)
                .limitLeft(500.00)
                .changeDateTime(limitDT)
                .build();

        OffsetDateTime trOffsetDateTime =
                OffsetDateTime.of(2022,1,25,
                        6,0,0,0, ZoneOffset.ofTotalSeconds(21600));

        ExceedTransactionDto expectedCombinedDto = ExceedTransactionDto
                .builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .trCurrency(Currency.getInstance("USD"))
                .trSum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(trOffsetDateTime)
                .historicalLimitSum(1000.00)
                .limitSetDateTime(OffsetDateTime.of(limitDT,ZoneOffset.UTC))
                .build();


        ExceedTransactionDto combinedDto = mapper.fromTrAndProdLimit(debitTrDao,productLimitDao);
        Assertions.assertThat(combinedDto)
                .hasNoNullFieldsOrPropertiesExcept("limitCurrency");
    }

    @Test
    void checkTransactionAndServiceToDtoMapping(){

        ServiceMonthlyLimit serviceMonthlyLimit = ServiceMonthlyLimit
                .builder()
                .currentLimit(1000.00)
                .limitLeft(500.00)
                .changeDateTime(limitDT)
                .build();

        OffsetDateTime trOffsetDateTime =
                OffsetDateTime.of(2022,1,25,
                        6,0,0,0, ZoneOffset.ofTotalSeconds(21600));

        ExceedTransactionDto expectedCombinedDto = ExceedTransactionDto
                .builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .trCurrency(Currency.getInstance("USD"))
                .trSum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(trOffsetDateTime)
                .historicalLimitSum(1000.00)
                .limitSetDateTime(OffsetDateTime.of(limitDT,ZoneOffset.UTC))
                .build();


        ExceedTransactionDto combinedDto =
                mapper.fromTrAndServiceLimit(debitTrDao,serviceMonthlyLimit);
        Assertions.assertThat(combinedDto)
                .hasNoNullFieldsOrPropertiesExcept("limitCurrency");
    }
}
