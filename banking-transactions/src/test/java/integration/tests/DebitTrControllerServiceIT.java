package integration.tests;

import com.tosDev.ApplicationRunner;
import com.tosDev.enums.ExpenseCategory;
import com.tosDev.spring.jpa.entity.DebitTransaction;
import com.tosDev.spring.jpa.entity.ExchangeRate;
import com.tosDev.spring.jpa.repository.DebitTrRepository;
import com.tosDev.spring.jpa.repository.ExchangeRateRepository;
import integration.config.IntegrationTestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
@AllArgsConstructor
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(classes = {ApplicationRunner.class})
public class DebitTrControllerServiceIT extends IntegrationTestBase {

    private MockMvc mockMvc;
    private DebitTrRepository debitTrRepository;

    @MockBean
    ExchangeRateRepository exchangeRateRepoMock;

    private final String debitTrJson =
            """
            {
            "account_from": 123,
            "account_to": 9999999999,
            "currency_shortname": "KZT",
            "sum": 10000.45,
            "expense_category": "product",
            "datetime": "2022-01-30 00:00:00+06"
            }
            """;

    @BeforeEach
    void configure(){
        debitTrRepository.deleteAll();
    }


    @Test
    void receiveTrCheckSaveInDb() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .post("/api/v1/debit-tr/receive-tr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(debitTrJson);

        //Сначала проверяем, что до транзакции записи нет
        assertThat(debitTrRepository.findAll()).isEmpty();
        ExchangeRate usdRateMock = mock(ExchangeRate.class);
        ExchangeRate trRateMock = mock(ExchangeRate.class);
        when(exchangeRateRepoMock.findByCurrencyCode("USD".toCharArray()))
                .thenReturn(Optional.of(usdRateMock));
        when(exchangeRateRepoMock.findByCurrencyCode("KZT".toCharArray()))
                .thenReturn(Optional.of(trRateMock));
        when(usdRateMock.getActualRate()).thenReturn(92.6118);

        mockMvc.perform(builder);

        //Фиксируем сохранение
        assertThat(debitTrRepository.findAll()).hasSize(1);
    }
    @Test
    void receiveTrCheckSavedEntityInDb() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .post("/api/v1/debit-tr/receive-tr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(debitTrJson);
        mockMvc.perform(builder);


        Optional<DebitTransaction> debitTransaction = debitTrRepository.findAll().stream().findFirst();
        if (debitTransaction.isPresent()){
            LocalDateTime localDateTime =
                    LocalDateTime.of(2022,1,30,
                            0,0,0,0);

            //Получаем сохраненную сущность
            DebitTransaction createdDao = debitTransaction.get();

            //Создаем mock ожидаемого сотояния сохраненной сущности
            DebitTransaction expectedDao = new DebitTransaction();
            expectedDao.setId(createdDao.getId());
            expectedDao.setAccountFrom(123L);
            expectedDao.setAccountTo(9999999999L);
            expectedDao.setCurrencyCode(new char[]{'K', 'Z', 'T'});
            expectedDao.setSum(10000.45);
            expectedDao.setExpenseCategory(ExpenseCategory.PRODUCT);
            expectedDao.setTrDateTime(localDateTime);
            expectedDao.setTimeZoneSecOffset(21600);
            expectedDao.setLimitExceeded(true);


            //Сравниваем
            assertEquals(expectedDao,createdDao);
        }
    }

}
