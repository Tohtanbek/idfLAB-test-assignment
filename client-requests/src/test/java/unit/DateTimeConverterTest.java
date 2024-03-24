package unit;

import com.tosDev.util.UtcConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DateTimeConverterTest {

    /**
     * Проверка конвертации локального времени и положительный offset в utc формат времени
     */
    @Test
    void convertLocalAndOffsetPositiveToUtc(){

        LocalDateTime localDateTime =
                LocalDateTime.of(2022,1,30,
                        6,0,0,0);

        int offsetSeconds = 21600;

        LocalDateTime utcLocalDateTime =
                UtcConverter.convertDateTimeToUTC(localDateTime,offsetSeconds);
        LocalDateTime expectedUtcDateTime = LocalDateTime.of(2022,1,30,
                        0,0,0,0);

        Assertions.assertEquals(expectedUtcDateTime,utcLocalDateTime);
    }

    /**
     * Проверка конвертации локального времени и положительный offset в utc формат времени
     */
    @Test
    void convertLocalAndOffsetNegativeToUtc(){

        LocalDateTime localDateTime =
                LocalDateTime.of(2022,1,30,
                        6,0,0,0);

        int offsetSeconds = -21600;

        LocalDateTime utcLocalDateTime =
                UtcConverter.convertDateTimeToUTC(localDateTime,offsetSeconds);

        LocalDateTime expectedUtcDateTime = LocalDateTime.of(2022,1,30,
                12,0,0,0);

        Assertions.assertEquals(expectedUtcDateTime,utcLocalDateTime);
    }
}
