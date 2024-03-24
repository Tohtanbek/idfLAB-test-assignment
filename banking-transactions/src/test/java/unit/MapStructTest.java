package unit;

import com.tosDev.dto.DebitTrDto;
import com.tosDev.dto.ExchangeRateDto;
import com.tosDev.enums.ExpenseCategory;
import com.tosDev.spring.config.BeanConfig;
import com.tosDev.spring.jpa.entity.DebitTransaction;
import com.tosDev.spring.jpa.entity.ExchangeRate;
import com.tosDev.util.mapstruct.MapStructMapper;
import com.tosDev.util.mapstruct.MapStructMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {MapStructMapperImpl.class, BeanConfig.class})
public class MapStructTest {
    @Autowired
    private MapStructMapper mapStructMapper;

    /**
     * Проверка работы кастомного MapStruct
     * Перевод из DAO в DTO
     */
    @Test
    void checkDebitTrDaoToDtoMapping() {

        LocalDateTime localDateTime =
                LocalDateTime.of(2022,1,30,
                        6,0,0,0);

        DebitTransaction debitTrDao = DebitTransaction
                .builder()
                .id(1L)
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyCode(new char[]{'U', 'S', 'D'})
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(localDateTime)
                .timeZoneSecOffset(21600)
                .limitExceeded(false)
                .build();

        OffsetDateTime offsetDateTime =
                OffsetDateTime.of(2022,1,30,
                        6,0,0,0,
                        ZoneOffset.ofHours(6));

        DebitTrDto expectedDto = DebitTrDto
                .builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currency(Currency.getInstance("USD"))
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .dateTime(offsetDateTime)
                .build();

        //Мапим с помощью MapStruct библиотеки
        DebitTrDto mappingResult = mapStructMapper.debitDaoToDebitDto(debitTrDao);

        assertEquals(expectedDto,mappingResult);
    }

    /**
     * Проверка работы кастомного MapStruct
     * Перевод из DTO в DAO
     */
    @Test
    void checkDebitTrDtoToDaoMapping() {

        LocalDateTime localDateTime =
                LocalDateTime.of(2022,1,30,
                        6,0,0,0);

        DebitTransaction expectedDao = DebitTransaction
                .builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyCode(new char[]{'U', 'S', 'D'})
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(localDateTime)
                .timeZoneSecOffset(21600)
                .limitExceeded(false)
                .build();

        OffsetDateTime offsetDateTime =
                OffsetDateTime.of(2022,1,30,
                        6,0,0,0,
                        ZoneOffset.ofHours(6));

        DebitTrDto debitTrDto = DebitTrDto
                .builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currency(Currency.getInstance("USD"))
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .dateTime(offsetDateTime)
                .build();

        //Мапим с помощью MapStruct библиотеки
        DebitTransaction mappingResult = mapStructMapper.debitDtoToDebitDao(debitTrDto);

        assertEquals(expectedDao,mappingResult);
    }

    @Test
    void checkExchangeDtoToDaoMapping(){

        OffsetDateTime offsetDateTime =
                OffsetDateTime.of(2024, 3, 22, 20,
                        0, 0, 0, ZoneOffset.ofHours(3));
        LocalDateTime localDateTime =
                LocalDateTime.of(2024, 3, 22, 20,
                        0, 0, 0);

        ExchangeRate expectedDao =
                ExchangeRate.builder()
                        .currencyCode(new char[]{'U','S','D'})
                        .actualRate(90.9076)
                        .accessDateTime(localDateTime)
                        .build();

        ExchangeRateDto dto = ExchangeRateDto.builder()
                .currency(Currency.getInstance("USD"))
                .actualRate(90.9076)
                .accessDateTime(offsetDateTime)
                .build();

        ExchangeRate resultDao = mapStructMapper.exchangeRateDtoToDao(dto);

        assertEquals(expectedDao,resultDao);
    }


}
