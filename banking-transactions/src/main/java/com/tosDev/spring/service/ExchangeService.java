package com.tosDev.spring.service;

import com.tosDev.dto.ExchangeRateDto;
import com.tosDev.util.mapstruct.MapStructMapper;
import com.tosDev.spring.jpa.entity.ExchangeRate;
import com.tosDev.spring.jpa.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final MapStructMapper mapper;

    /**
     * Метод, сохраняющий в бд ежедневно загружаемый список курсов валют
     * @param dtoList список ExchangeRateDto с курсом по каждой валюте
     */
    @Transactional
    public void saveExchangeRateDto(List<ExchangeRateDto> dtoList){
        for (ExchangeRateDto dto : dtoList){
            ExchangeRate freshDao = mapper.exchangeRateDtoToDao(dto);
            Optional<ExchangeRate> existedChangeRate = exchangeRateRepository
                    .findByCurrencyCode(dto.getCurrency().getCurrencyCode().toCharArray());
            existedChangeRate.ifPresentOrElse(dao -> {
                freshDao.setId(dao.getId());
                exchangeRateRepository.save(freshDao);
            }
            ,() -> exchangeRateRepository.save(freshDao));
        }
    }
}
