package com.tosDev.client.util;

import java.time.LocalDateTime;

public class UtcConverter {

    private UtcConverter(){}

    /**
     * Утилитный класс, возвращает utc
     * на основе локального времени и отклонения от гринвича в секундах
     * @param localDateTime локальное время
     * @param offsetSeconds отклонение от utc (отрицательное или положительное)
     * @return localDateTime по utc
     */
    public static LocalDateTime convertDateTimeToUTC(LocalDateTime localDateTime,int offsetSeconds){
        if (offsetSeconds >= 0) {
            return localDateTime.minusSeconds(offsetSeconds);
        } else {
            return localDateTime.plusSeconds(Math.abs(offsetSeconds));
        }
    }
}
