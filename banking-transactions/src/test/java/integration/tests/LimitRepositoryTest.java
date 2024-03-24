package integration.tests;

import com.tosDev.tr.ApplicationRunner;
import com.tosDev.tr.spring.jpa.entity.ProductMonthlyLimit;
import com.tosDev.tr.spring.jpa.entity.ServiceMonthlyLimit;
import com.tosDev.tr.spring.jpa.repository.ProductMonthlyLimitRepository;
import com.tosDev.tr.spring.jpa.repository.ServiceMonthlyLimitRepository;
import integration.config.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ApplicationRunner.class})
@EnableAutoConfiguration
@ActiveProfiles({"test"})
public class LimitRepositoryTest extends IntegrationTestBase {
    @Autowired
    ProductMonthlyLimitRepository productLimitRepo;
    @Autowired
    ServiceMonthlyLimitRepository serviceLimitRepo;

    @BeforeEach
    void loadDefaultLimits(){
        serviceLimitRepo.save(ServiceMonthlyLimit
                .builder()
                .id(1)
                .currentLimit(1000.00)
                .limitLeft(1000.00)
                .changeDateTime(LocalDateTime.of(2024,5,5,15,15))
                .build());

        productLimitRepo.save(ProductMonthlyLimit
                .builder()
                .id(1)
                .currentLimit(1000.00)
                .limitLeft(1000.00)
                .changeDateTime(LocalDateTime.of(2024,5,5,15,15))
                .build());
    }

    /**
     * Query, уменьшающий остаток по лимиту для product транзакций
     */
    @Test
    void testProductRepoCorrectLimit(){
        ProductMonthlyLimit productLimit = productLimitRepo.findByLastDateTime().orElseThrow();
        Integer id = productLimit.getId();
        Double startingLimit = productLimit.getLimitLeft();
        assertThat(startingLimit).isEqualTo(1000.00);

        //Проверяем уменьшение лимита после транзакции
        Double transactionSum = 600.00;
        productLimitRepo.correctLimit(transactionSum,id);
        ProductMonthlyLimit updatedLimit = productLimitRepo.findById(id).orElseThrow();
        assertThat(updatedLimit.getLimitLeft())
                .isEqualTo(startingLimit - transactionSum);
    }

    /**
     * Query, уменьшающий остаток по лимиту для service транзакций
     */
    @Test
    void testServiceRepoCorrectLimit(){
        ServiceMonthlyLimit serviceLimit = serviceLimitRepo.findByLastDateTime().orElseThrow();
        Integer id = serviceLimit.getId();
        Double startingLimit = serviceLimit.getLimitLeft();
        assertThat(startingLimit).isEqualTo(1000.00);

        //Проверяем уменьшение лимита после транзакции
        Double transactionSum = 600.00;
        serviceLimitRepo.correctLimit(transactionSum,id);
        ServiceMonthlyLimit updatedLimit = serviceLimitRepo.findById(id).orElseThrow();
        assertThat(updatedLimit.getLimitLeft())
                .isEqualTo(startingLimit - transactionSum);
    }

    /**
     * Query, проверяющий отрицательный лимит у product транзакций
     */
    @Test
    void testIsExceededProductQuery(){
        boolean expectNotExceeded = productLimitRepo.checkIfNegativeLimit(1);
        productLimitRepo.correctLimit(1000.50,1);
        boolean expectExceeded = productLimitRepo.checkIfNegativeLimit(1);

        assertFalse(expectNotExceeded);
        assertTrue(expectExceeded);
    }

    /**
     * Query, проверяющий отрицательный лимит у service транзакций
     */
    @Test
    void testIsExceededServiceQuery(){

        boolean expectNotExceeded = serviceLimitRepo.checkIfNegativeLimit(1);
        serviceLimitRepo.correctLimit(1000.50,1);
        boolean expectExceeded = serviceLimitRepo.checkIfNegativeLimit(1);

        assertFalse(expectNotExceeded);
        assertTrue(expectExceeded);
    }

    /**
     * Проверка метода выдачи сущности по ближайшей дате
     */
    @Test
    void testFindByLastDate(){
        serviceLimitRepo.save(ServiceMonthlyLimit
                .builder()
                .limitLeft(1000.00)
                .currentLimit(1000.00)
                .changeDateTime(LocalDateTime.of(2024,5,4,15,15))
                .build());

        productLimitRepo.save(ProductMonthlyLimit
                .builder()
                .limitLeft(1000.00)
                .currentLimit(1000.00)
                .changeDateTime(LocalDateTime.of(2024,5,4,15,15))
                .build());

        ServiceMonthlyLimit serviceLimit = serviceLimitRepo.findByLastDateTime().orElseThrow();
        ProductMonthlyLimit productLimit = productLimitRepo.findByLastDateTime().orElseThrow();

        //Проверяем, что несмотря на сохраненные значения, в выдаче сущности по последней дате
        assertThat(serviceLimit.getChangeDateTime().getDayOfMonth()).isEqualTo(5);
        assertThat(productLimit.getChangeDateTime().getDayOfMonth()).isEqualTo(5);
    }
}
