package integration.tests;

import com.tosDev.spring.config.GeneralConfig;
import com.tosDev.spring.rest.feign.ExchangeClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {GeneralConfig.class})
@EnableFeignClients(basePackages = {"com.tosDev.spring.rest.feign"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles({"test"})
public class FeignRatesLoaderIT {


    @Autowired
    private ExchangeClient client;


    //Тестируем feign на загрузку котировок из стороннего api
    @Test
    void testRatesLoading(){
        ResponseEntity<String> response =
                client.loadActualRates();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        String expectedBodyStart = "{\n" + "    \"Date\":";
        assertTrue(response.getBody().startsWith(expectedBodyStart));
    }
}
