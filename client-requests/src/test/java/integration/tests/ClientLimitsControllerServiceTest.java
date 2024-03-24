package integration.tests;

import com.tosDev.tr.ApplicationRunner;
import com.tosDev.tr.enums.ExpenseCategory;
import com.tosDev.tr.spring.jpa.entity.DebitTransaction;
import com.tosDev.tr.spring.jpa.entity.ProductMonthlyLimit;
import com.tosDev.tr.spring.jpa.entity.ServiceMonthlyLimit;
import com.tosDev.tr.spring.jpa.repository.DebitTrRepository;
import com.tosDev.tr.spring.jpa.repository.ProductMonthlyLimitRepository;
import com.tosDev.tr.spring.jpa.repository.ServiceMonthlyLimitRepository;
import com.tosDev.tr.util.exchange.DailyExchangeLoader;
import com.tosDev.tr.util.mapstruct.MapStructMapperImpl;
import integration.config.IntegrationTestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@AllArgsConstructor
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(classes = {ApplicationRunner.class,})
public class ClientLimitsControllerServiceTest extends IntegrationTestBase {

    private MockMvc mockMvc;
    @MockBean
    DailyExchangeLoader dailyExchangeLoader;
    @MockBean
    MapStructMapperImpl mock;

    private ProductMonthlyLimitRepository productLimitRepo;
    private ServiceMonthlyLimitRepository serviceLimitRepo;

    private DebitTrRepository debitTrRepository;

    static String showExceedExpectedResponse;

    @BeforeAll
    static void configureResponse() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("expected_show_exceed_result.txt")) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()){
                sb.append(br.readLine());
            }
            showExceedExpectedResponse = sb.toString();
        }
    }

    @BeforeEach
    void configure() {
        LocalDateTime prevDateTime = LocalDateTime.of(2024, 3, 3, 20, 0);
        productLimitRepo.deleteAll();
        serviceLimitRepo.deleteAll();
        productLimitRepo.save(
                ProductMonthlyLimit
                        .builder()
                        .id(1)
                        .currentLimit(1000.00)
                        .limitLeft(-100.00)
                        .changeDateTime(prevDateTime)
                        .build());
        serviceLimitRepo.save(
                ServiceMonthlyLimit
                        .builder()
                        .id(1)
                        .currentLimit(1000.00)
                        .limitLeft(-100.00)
                        .changeDateTime(prevDateTime)
                        .build());
    }


    @Test
    void receiveFreshLimitOfProductTestSave() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .post("/api/v1/client-limits/set-fresh-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("category", "0")
                        .param("limit", "2000");

        //Сначала проверяем, что до транзакции записи нет
        assertThat(productLimitRepo.findAll()).hasSize(1);
        assertThat(serviceLimitRepo.findAll()).hasSize(1);

        mockMvc.perform(builder);

        //Фиксируем сохранение нового лимита продуктов
        assertThat(productLimitRepo.findAll()).hasSize(2);
        //Фиксируем отсутствие сохранения нового лимита сервисов
        assertThat(serviceLimitRepo.findAll()).hasSize(1);
    }

    @Test
    void receiveFreshLimitOfServiceTestSave() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .post("/api/v1/client-limits/set-fresh-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("category", "1")
                        .param("limit", "2000");

        //Сначала проверяем, что до транзакции записи нет
        assertThat(productLimitRepo.findAll()).hasSize(1);
        assertThat(serviceLimitRepo.findAll()).hasSize(1);

        mockMvc.perform(builder);

        //Фиксируем сохранение нового лимита сервисов
        assertThat(serviceLimitRepo.findAll()).hasSize(2);
        //Фиксируем отсутствие сохранения нового лимита продуктов
        assertThat(productLimitRepo.findAll()).hasSize(1);

    }

    /**
     * Тест корректных данных в полях категории product
     * нового лимита
     */
    @Test
    void checkProductUpdatedLimitFields() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .post("/api/v1/client-limits/set-fresh-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("category", "0")
                        .param("limit", "2000");

        mockMvc.perform(builder);

        ProductMonthlyLimit productLimit = productLimitRepo.findByLastDateTime().orElseThrow();
        Double currentLimit = productLimit.getCurrentLimit();
        Double actualLimitLeft = productLimit.getLimitLeft();
        assertEquals(2000.00,currentLimit);
        assertEquals(900.00,actualLimitLeft);
    }

    /**
     * Тест корректных данных в полях категории service
     * нового лимита
     */
    @Test
    void checkServiceUpdatedLimitFields() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .post("/api/v1/client-limits/set-fresh-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("category", "1")
                        .param("limit", "2000");

        mockMvc.perform(builder);

        ServiceMonthlyLimit serviceMonthlyLimit = serviceLimitRepo.findByLastDateTime().orElseThrow();
        Double currentLimit = serviceMonthlyLimit.getCurrentLimit();
        Double actualLimitLeft = serviceMonthlyLimit.getLimitLeft();
        assertEquals(2000.00,currentLimit);
        assertEquals(900.00,actualLimitLeft);
    }

    //Метод ищет ближайшую предыдущую смену лимита по дате транзакции по гринвичу
    //В сменах даты по гринвичу хранятся, потому что сервис сохраняет их сам по своему времени.
    @Test
    void checkFindByClosestDate(){

        LocalDateTime closestLimitDt =
                LocalDateTime.of(2024, 3, 3, 22, 0);
        productLimitRepo.save(
                ProductMonthlyLimit
                        .builder()
                        .id(1)
                        .currentLimit(1000.00)
                        .limitLeft(-100.00)
                        .changeDateTime(closestLimitDt)
                        .build());

        LocalDateTime transactionDT =
                LocalDateTime.of(2024, 3, 4, 20, 0);
        ProductMonthlyLimit closestLimit =
                productLimitRepo.findByClosestDate(transactionDT).orElseThrow();

        assertThat(closestLimit.getChangeDateTime()).isEqualTo(closestLimitDt);
    }

    @Test
    void checkShowExceedRows() throws Exception {

        loadTransactionsForShowExceed();
        assertThat(debitTrRepository.findAll()).hasSize(3);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .get("/api/v1/client-limits/show-exceed-transactions");

        MvcResult result =
                mockMvc.perform(builder)
                        .andExpect(status().is(200))
                        .andReturn();
        String resultBody = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(showExceedExpectedResponse,resultBody,true);
    }

    private void loadTransactionsForShowExceed(){

        debitTrRepository.deleteAll();

        LocalDateTime transactionDT =
                LocalDateTime.of(2024,4,4,
                        6,0,0,0);

        DebitTransaction debitTrDao = DebitTransaction
                .builder()
                .id(1L)
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyCode(new char[]{'K', 'Z', 'T'})
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(transactionDT)
                .timeZoneSecOffset(21600)
                .limitExceeded(false)
                .build();

        DebitTransaction debitTrDaoExceed1 = DebitTransaction
                .builder()
                .id(2L)
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyCode(new char[]{'K', 'Z', 'T'})
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(transactionDT.plusDays(1))
                .timeZoneSecOffset(21600)
                .limitExceeded(true)
                .build();

        DebitTransaction debitTrDaoExceed2 = DebitTransaction
                .builder()
                .id(3L)
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyCode(new char[]{'R', 'U', 'B'})
                .sum(10000.45)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .trDateTime(transactionDT.minusHours(10))
                .timeZoneSecOffset(21600)
                .limitExceeded(true)
                .build();


        debitTrRepository.save(debitTrDao);
        debitTrRepository.save(debitTrDaoExceed1);
        debitTrRepository.save(debitTrDaoExceed2);
    }

}
