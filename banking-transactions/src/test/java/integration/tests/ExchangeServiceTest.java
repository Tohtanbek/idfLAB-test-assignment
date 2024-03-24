package integration.tests;

import com.tosDev.tr.ApplicationRunner;
import com.tosDev.tr.dto.ExchangeRateDto;
import com.tosDev.tr.spring.jpa.repository.ExchangeRateRepository;
import com.tosDev.tr.spring.service.ExchangeService;
import integration.config.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ApplicationRunner.class})
@ActiveProfiles({"test"})
public class ExchangeServiceTest extends IntegrationTestBase {

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    static List<ExchangeRateDto> readyDtoList;

    @BeforeEach
    void configure(){
        exchangeRateRepository.deleteAll();
        OffsetDateTime odt = OffsetDateTime.of(2024, 3, 22, 20,
                0, 0, 0, ZoneOffset.ofHours(3));

        ExchangeRateDto dto1 =
                new ExchangeRateDto(Currency.getInstance("AUD"),
                        60.4292,odt);
        ExchangeRateDto dto2 =
                new ExchangeRateDto(Currency.getInstance("AMD"),
                        30.4292,odt);
        ExchangeRateDto dto3 =
                new ExchangeRateDto(Currency.getInstance("KZT"),
                        100.4292,odt);

        readyDtoList = List.of(dto1,dto2,dto3);
    }
    @Test
    void checkRatesSaved(){

        //Проверяем, что перед вызовом таблица пустая
        assertThat(exchangeRateRepository.findAll()).isEmpty();
        exchangeService.saveExchangeRateDto(readyDtoList);
        //Проверяем, что три сущности загружены
        assertThat(exchangeRateRepository.findAll()).hasSize(3);
    }
    @Test
    void checkRatesUpdated(){

        OffsetDateTime prevOdt = OffsetDateTime.of(2024, 3, 22, 20,
                0, 0, 0, ZoneOffset.ofHours(3));
        OffsetDateTime odt = OffsetDateTime.of(2024, 3, 23, 20,
                0, 0, 0, ZoneOffset.ofHours(3));

        ExchangeRateDto upDto1 =
                new ExchangeRateDto(Currency.getInstance("AUD"),
                        120.4292,odt);
        ExchangeRateDto udDto2 =
                new ExchangeRateDto(Currency.getInstance("AMD"),
                        60.4292,odt);
        ExchangeRateDto udDto3 =
                new ExchangeRateDto(Currency.getInstance("KZT"),
                        200.4292,odt);

        List<ExchangeRateDto> dtoListFreshUpdate = List.of(upDto1,udDto2,udDto3);

        //Сначала загружаем курсы прошлого дня
        assertThat(exchangeRateRepository.findAll()).isEmpty();
        exchangeService.saveExchangeRateDto(readyDtoList);
        //Проверяем
        assertThat(exchangeRateRepository.findAll()).hasSize(3);
        assertThat(exchangeRateRepository.findAll())
                .anyMatch(dao -> dao.getActualRate()==30.43);
        assertThat(exchangeRateRepository.findAll())
                .noneMatch(dao -> dao.getActualRate()==120.43);
        //Затем загружаем сущности как будто на следующий день, которые должны обновиться
        exchangeService.saveExchangeRateDto(dtoListFreshUpdate);

        //Проверяем, что сущности перезаписаны
        assertThat(exchangeRateRepository.findAll()).hasSize(3);

        //Проверяем, что значения актуальные
        assertThat(exchangeRateRepository.findAll())
                .allMatch(dao -> dao.getAccessDateTime().equals(odt.toLocalDateTime()));
        assertThat(exchangeRateRepository.findAll())
                .noneMatch(dao -> dao.getAccessDateTime().equals(prevOdt.toLocalDateTime()));

        assertThat(exchangeRateRepository.findAll())
                .anyMatch(dao -> dao.getActualRate()==200.43);
    }
}
