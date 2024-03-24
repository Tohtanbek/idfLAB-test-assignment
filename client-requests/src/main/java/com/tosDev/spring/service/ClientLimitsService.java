package com.tosDev.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.ExceedTransactionDto;
import com.tosDev.enums.ExpenseCategory;
import com.tosDev.spring.jpa.entity.DebitTransaction;
import com.tosDev.spring.jpa.entity.ProductMonthlyLimit;
import com.tosDev.spring.jpa.entity.ServiceMonthlyLimit;
import com.tosDev.spring.jpa.repository.DebitTrRepository;
import com.tosDev.spring.jpa.repository.ProductMonthlyLimitRepository;
import com.tosDev.spring.jpa.repository.ServiceMonthlyLimitRepository;
import com.tosDev.util.UtcConverter;
import com.tosDev.util.mapstruct.MapStructMapperClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientLimitsService {

    private final ServiceMonthlyLimitRepository serviceLimitRepo;
    private final ProductMonthlyLimitRepository productLimitRepo;
    private final ObjectMapper objectMapper;
    private final DebitTrRepository debitTrRepository;
    private final MapStructMapperClient mapStructMapper;

    public ResponseEntity<Void> setFreshLimit(Integer category,Double freshLimit){
        ExpenseCategory expenseCategory = ExpenseCategory.values()[category];
        try {
            saveLimit(expenseCategory,freshLimit);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            log.error("Ошибка при сохранении нового лимита");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Transactional
    public void saveLimit(ExpenseCategory expenseCategory,Double freshLimit){
        switch (expenseCategory) {
            case PRODUCT -> {
                ProductMonthlyLimit prevProdLimit =
                        productLimitRepo.findByLastDateTime().orElseThrow();
                //Считаем остаток, исходя из нового лимита
                Double prevLimit = prevProdLimit.getCurrentLimit();
                Double prevLimitLeft = prevProdLimit.getLimitLeft();
                Double actualLimitLeft = (freshLimit - prevLimit) + prevLimitLeft;
                //Сохраняем новый лимит с актуальным остатком
                ProductMonthlyLimit freshProductLimit =
                        ProductMonthlyLimit
                                .builder()
                                .currentLimit(freshLimit)
                                .limitLeft(actualLimitLeft)
                                .changeDateTime(LocalDateTime.now(ZoneOffset.UTC))
                                .build();
                productLimitRepo.save(freshProductLimit);
                log.info("Лимит на продукты обновлен: {}",prevProdLimit);
            }
            case SERVICE -> {
                ServiceMonthlyLimit prevServiceLimit =
                        serviceLimitRepo.findByLastDateTime().orElseThrow();
                //Считаем остаток, исходя из нового лимита
                Double prevLimit = prevServiceLimit.getCurrentLimit();
                Double prevLimitLeft = prevServiceLimit.getLimitLeft();
                Double actualLimitLeft = (freshLimit - prevLimit) + prevLimitLeft;
                //Сохраняем новый лимит с актуальным остатком
                ServiceMonthlyLimit freshServiceLimit =
                        ServiceMonthlyLimit
                                .builder()
                                .currentLimit(freshLimit)
                                .limitLeft(actualLimitLeft)
                                .changeDateTime(LocalDateTime.now(ZoneOffset.UTC))
                                .build();
                serviceLimitRepo.save(freshServiceLimit);
                log.info("Лимит на сервисы обновлен: {}",prevServiceLimit);
            }
        }
    }

    /**
     * Получает из бд сущность транзакций и лимитов, комбинирует их
     * в одну dto, формирует из них список на вывод клиенту
     */
    @Transactional
    public ResponseEntity<String> showExceedRows(){
        try {
            List<ExceedTransactionDto> exceedTransactionDtoList = new ArrayList<>();
            List<DebitTransaction> exceedTransactions =
                    debitTrRepository.findAllByLimitExceededTrue();
            //Итерируемся по всем транзакциям с превышенным лимитом и создаем дто
            for (DebitTransaction debitTransaction : exceedTransactions){
                exceedTransactionDtoList.add(mapTransactionAndJoinLimits(debitTransaction));
            }
            String body = objectMapper.writeValueAsString(exceedTransactionDtoList);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Не удалось загрузить клиенту данные о транзакциях, превысивших лимит");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Метод получает превысившую лимит транзакцию и возвращает
     * скомбинированный dto из транзакции и лимита, который был превышен
     * @param debitTransaction превысившая лимит транзакция
     * @return dto транзакции и лимита
     */
    ExceedTransactionDto mapTransactionAndJoinLimits(DebitTransaction debitTransaction){
        //Получаем у превысившей транзакции категорию, а также переводим ее время в utc
        ExpenseCategory expenseCategory = debitTransaction.getExpenseCategory();
        LocalDateTime trLocalDT = debitTransaction.getTrDateTime();
        Integer offset = debitTransaction.getTimeZoneSecOffset();
        LocalDateTime utcDateTime =  UtcConverter.convertDateTimeToUTC(trLocalDT,offset);

        //В зависимости от категории возвращаем комбинированный dto на выход
        ExceedTransactionDto combinedDto =
        switch (expenseCategory) {
            case PRODUCT -> {
               ProductMonthlyLimit productLimit =
                       productLimitRepo.findByClosestDate(utcDateTime).orElseThrow();
               yield mapStructMapper.fromTrAndProdLimit(debitTransaction,productLimit);
            }
            case SERVICE -> {
                ServiceMonthlyLimit serviceLimit =
                        serviceLimitRepo.findByClosestDate(utcDateTime).orElseThrow();
                yield mapStructMapper.fromTrAndServiceLimit(debitTransaction,serviceLimit);
            }
        };
        //Устанавливаем для вывода поле валюты расчета лимита (всегда доллар по условию)
        combinedDto.setLimitCurrency(Currency.getInstance("USD"));
        return combinedDto;
    }

}
