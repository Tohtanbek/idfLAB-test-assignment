package com.tosDev.tr.spring.service;

import com.tosDev.tr.spring.jpa.repository.DebitTrRepository;
import com.tosDev.tr.dto.DebitTrDto;
import com.tosDev.tr.enums.ExpenseCategory;
import com.tosDev.tr.util.mapstruct.MapStructMapper;
import com.tosDev.tr.spring.jpa.entity.DebitTransaction;
import com.tosDev.tr.spring.jpa.entity.ExchangeRate;
import com.tosDev.tr.spring.jpa.entity.ProductMonthlyLimit;
import com.tosDev.tr.spring.jpa.entity.ServiceMonthlyLimit;
import com.tosDev.tr.spring.jpa.repository.ExchangeRateRepository;
import com.tosDev.tr.spring.jpa.repository.ProductMonthlyLimitRepository;
import com.tosDev.tr.spring.jpa.repository.ServiceMonthlyLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebitTrService {
    private final DebitTrRepository debitTransactionRepo;

    private final ExchangeRateRepository exchangeRateRepo;
    private final ProductMonthlyLimitRepository productLimitRepo;
    private final ServiceMonthlyLimitRepository serviceLimitRepo;

    private final MapStructMapper mapStructMapper;

    @Transactional
    public ResponseEntity<Void> mapAndSaveTr(DebitTrDto debitTrDto){
        try {
            //Создаем dao entity
            DebitTransaction debitTr = mapStructMapper.debitDtoToDebitDao(debitTrDto);
            //Конвертируем для расчета остатка по лимиту сумму в USD
            Double usdSum =
                    convertSumToUsd(debitTr.getCurrencyCode(), debitTrDto.getSum());
            //Сначала корректируем в бд остаток по лимиту и узнаем превышен ли остаток в USD
            boolean exceeded =
                    correctLeftLimit(debitTr.getExpenseCategory(), usdSum);
            //Сохраняем целостную сущность
            debitTr.setLimitExceeded(exceeded);
            debitTr = debitTransactionRepo.save(debitTr);
            log.info("Сохранена транзакция по id {}",debitTr.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка сохранения транзакции {}",debitTrDto);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Вызывает изменение остатка лимита в бд
     * на основе суммы и категории транзакции
     * @param category enum категории (product или service)
     * @param sum сумма в !USD!
     * @return boolean выход за рамки лимита
     */
    @Transactional
    public Boolean correctLeftLimit(ExpenseCategory category,Double sum){
        return switch (category) {
            case SERVICE -> {
                ServiceMonthlyLimit serviceLimit =
                        serviceLimitRepo.findByLastDateTime().orElseThrow();
                Integer id = serviceLimit.getId();
                serviceLimitRepo.correctLimit(sum,id);
                yield serviceLimitRepo.checkIfNegativeLimit(id);
            }
            case PRODUCT -> {
                ProductMonthlyLimit productLimit =
                        productLimitRepo.findByLastDateTime().orElseThrow();
                Integer id = productLimit.getId();
                productLimitRepo.correctLimit(sum,id);
                yield productLimitRepo.checkIfNegativeLimit(id);
            }
        };
    }

    /**
     * Конвертирует сумму транзакции в USD, чтобы далее
     * проверить выход за пределы лимита трат
     * @param currencyCode Код валюты транзакции
     * @param sum Сумма транзакции в оригинальной валюте
     * @return сумма транзакции в USD
     */
    @Transactional
    public Double convertSumToUsd(char[] currencyCode, Double sum){

        //Сначала проверяем валюту транзакции
        Currency trCurrency = Currency.getInstance(new String(currencyCode));

        //Если рубль, то пропускаем конвертацию в рубли
        if (trCurrency.getCurrencyCode().equals("RUB")){
            ExchangeRate usdDao =
                    exchangeRateRepo.findByCurrencyCode("USD".toCharArray()).orElseThrow();
            Double usdRate = usdDao.getActualRate();
            return sum/usdRate;
        }
        //Если доллар, то сразу возвращаем, конвертировать не надо
        else if (trCurrency.getCurrencyCode().equals("USD")) {
            return sum;
        }
        //Если другие валюты, то конвертируем сначала в рубли, потом в доллары США
        else {
            //Сначала получаем курс доллара
            ExchangeRate usdDao =
                    exchangeRateRepo.findByCurrencyCode("USD".toCharArray()).orElseThrow();
            Double usdRate = usdDao.getActualRate();

            //Далее получаем курс валюты транзакции в рублях
            ExchangeRate transactionCurrDao =
                    exchangeRateRepo.findByCurrencyCode(currencyCode).orElseThrow();
            Double transactionCurrRate = transactionCurrDao.getActualRate();

            //Получаем сумму транзакции в рублях
            Double rubSum = transactionCurrRate * sum;

            //Переводим рубли в доллары
            return rubSum/usdRate;
        }
    }

}
