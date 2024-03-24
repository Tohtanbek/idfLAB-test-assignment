package unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.ExchangeRateDto;
import com.tosDev.spring.rest.feign.ExchangeClient;
import com.tosDev.spring.service.ExchangeService;
import com.tosDev.util.exchange.DailyExchangeLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DailyExchangeLoaderTest {

    @MockBean
    ExchangeClient exchangeClientMock;
    static DateTimeFormatter dateTimeFormatter;
    @MockBean
    ExchangeService exchangeServiceMock;
    @MockBean
    ObjectMapper objectMapperMock;

    static String jsonNodeStr;

    @BeforeAll
    static void configure() throws IOException {
        dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("api_tree_test.txt")) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()){
                sb.append(br.readLine());
            }
            jsonNodeStr = sb.toString();
        }
    }

    @Test
    void mapJsonNodeToDtoList() throws ClassNotFoundException, NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = classLoader.loadClass("com.tosDev.util.exchange.DailyExchangeLoader");
        Method mapJsonToDtoListMethod =
                clazz.getDeclaredMethod("mapJsonNodeToDtoList", JsonNode.class);
        mapJsonToDtoListMethod.setAccessible(true);

        DailyExchangeLoader loaderMock =
                new DailyExchangeLoader(exchangeClientMock,objectMapperMock,
                        dateTimeFormatter,exchangeServiceMock);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonNodeStr);

        List<?> result = (List<?>) mapJsonToDtoListMethod.invoke(loaderMock,rootNode);

        //Доступно 43 валюты
        assertThat(result.size()).isEqualTo(43);

        List<String> codes = result.stream().map(dto -> (ExchangeRateDto) dto)
                .map(ExchangeRateDto::getCurrency)
                .map(Currency::getCurrencyCode).toList();

        //Доступны все валюты из json
        assertThat(codes).anyMatch(code -> code.equals("AUD"));
        assertThat(codes).anyMatch(code -> code.equals("AZN"));
        assertThat(codes).anyMatch(code -> code.equals("GBP"));
        assertThat(codes).anyMatch(code -> code.equals("AMD"));
        assertThat(codes).anyMatch(code -> code.equals("BYN"));
        assertThat(codes).anyMatch(code -> code.equals("BGN"));
        assertThat(codes).anyMatch(code -> code.equals("BRL"));
        assertThat(codes).anyMatch(code -> code.equals("HUF"));
        assertThat(codes).anyMatch(code -> code.equals("VND"));
        assertThat(codes).anyMatch(code -> code.equals("HKD"));
        assertThat(codes).anyMatch(code -> code.equals("GEL"));
        assertThat(codes).anyMatch(code -> code.equals("DKK"));
        assertThat(codes).anyMatch(code -> code.equals("AED"));
        assertThat(codes).anyMatch(code -> code.equals("USD"));
        assertThat(codes).anyMatch(code -> code.equals("EUR"));
        assertThat(codes).anyMatch(code -> code.equals("EGP"));
        assertThat(codes).anyMatch(code -> code.equals("INR"));
        assertThat(codes).anyMatch(code -> code.equals("IDR"));
        assertThat(codes).anyMatch(code -> code.equals("KZT"));
        assertThat(codes).anyMatch(code -> code.equals("CAD"));
        assertThat(codes).anyMatch(code -> code.equals("QAR"));
        assertThat(codes).anyMatch(code -> code.equals("KGS"));
        assertThat(codes).anyMatch(code -> code.equals("CNY"));
        assertThat(codes).anyMatch(code -> code.equals("MDL"));
        assertThat(codes).anyMatch(code -> code.equals("NZD"));
        assertThat(codes).anyMatch(code -> code.equals("NOK"));
        assertThat(codes).anyMatch(code -> code.equals("PLN"));
        assertThat(codes).anyMatch(code -> code.equals("RON"));
        assertThat(codes).anyMatch(code -> code.equals("XDR"));
        assertThat(codes).anyMatch(code -> code.equals("SGD"));
        assertThat(codes).anyMatch(code -> code.equals("TJS"));
        assertThat(codes).anyMatch(code -> code.equals("THB"));
        assertThat(codes).anyMatch(code -> code.equals("TRY"));
        assertThat(codes).anyMatch(code -> code.equals("TMT"));
        assertThat(codes).anyMatch(code -> code.equals("UZS"));
        assertThat(codes).anyMatch(code -> code.equals("UAH"));
        assertThat(codes).anyMatch(code -> code.equals("CZK"));
        assertThat(codes).anyMatch(code -> code.equals("SEK"));
        assertThat(codes).anyMatch(code -> code.equals("CHF"));
        assertThat(codes).anyMatch(code -> code.equals("RSD"));
        assertThat(codes).anyMatch(code -> code.equals("ZAR"));
        assertThat(codes).anyMatch(code -> code.equals("KRW"));
        assertThat(codes).anyMatch(code -> code.equals("JPY"));
    }
}