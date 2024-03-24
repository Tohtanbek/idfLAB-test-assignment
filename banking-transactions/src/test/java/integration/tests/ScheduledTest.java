package integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tosDev.ApplicationRunner;
import com.tosDev.spring.config.GeneralConfig;
import com.tosDev.spring.rest.feign.ExchangeClient;
import com.tosDev.spring.service.ExchangeService;
import com.tosDev.util.exchange.DailyExchangeLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {GeneralConfig.class,DailyExchangeLoader.class})
@ContextConfiguration
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class ScheduledTest {
    @MockBean
    ExchangeClient mockFeignClient;
    @MockBean
    @Qualifier("apiDateFormatter")
    DateTimeFormatter dateTimeFormatter;
    @MockBean
    ExchangeService exchangeService;
    @MockBean
    ObjectMapper objectMapper;
    @Autowired
    private ScheduledTaskHolder scheduledTaskHolder;

    @Autowired
    DailyExchangeLoader dailyExchangeLoader;


    /**
     * Тестируем, что в контексте @Scheduled есть наша задача по обновлению курса валют
     */
    @Test
    public void testYearlyCronTaskScheduled() {
        Set<ScheduledTask> scheduledTasks = scheduledTaskHolder.getScheduledTasks();
        scheduledTasks.forEach(scheduledTask ->
                scheduledTask.getTask().getRunnable().getClass().getDeclaredMethods());
        long count = scheduledTasks.stream()
                .filter(scheduledTask -> scheduledTask.getTask() instanceof CronTask)
                .map(scheduledTask -> (CronTask) scheduledTask.getTask())
                .count();
        Assertions.assertThat(count).isEqualTo(1L);
    }

    /**
     * Тестируем вызов из ежедневного загрузчика сервиса на сохранение валют в бд
     */
    @Test
    void checkServiceToSaveInvocation() throws JsonProcessingException,
            NoSuchMethodException {

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String jsonNodeStr;
        try (InputStream is = classLoader.getResourceAsStream("api_tree_test.txt")) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()){
                sb.append(br.readLine());
            }
            jsonNodeStr = sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        when(mockFeignClient.loadActualRates()).thenReturn(ResponseEntity.ok(jsonNodeStr));
        dailyExchangeLoader.loadTodayRates();
        verify(exchangeService,times(1))
                .saveExchangeRateDto(any());
    }
}
