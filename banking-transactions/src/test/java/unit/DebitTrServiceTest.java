package unit;

import com.tosDev.spring.jpa.entity.ExchangeRate;
import com.tosDev.spring.service.DebitTrService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DebitTrServiceTest {

    /**
     * Unit тест конвертера суммы из валюты транзакции в USD
     */
    @Test
    void convertSumToUsd() {
        Double testSum = 5000.00;
        Double testUsdRate = 92.6118;
        Double testKztRate = 0.205225;

        Double expectedResult = (testSum*testKztRate)/testUsdRate;

        //Сначала получаем курс доллара
        ExchangeRate usdDaoMock = mock(ExchangeRate.class);
        when(usdDaoMock.getActualRate()).thenReturn(testUsdRate);
        Double usdRate = usdDaoMock.getActualRate();

        //Далее получаем курс валюты транзакции в рублях
        ExchangeRate transactionCurrDao = mock(ExchangeRate.class);
        when(transactionCurrDao.getActualRate()).thenReturn(testKztRate);
        Double transactionCurrRate = transactionCurrDao.getActualRate();

        //Получаем сумму транзакции в рублях
        Double rubSum = transactionCurrRate * testSum;

        //Переводим рубли в доллары
        Double usdResult =  rubSum/usdRate;

        assertEquals(expectedResult,usdResult);
    }
}