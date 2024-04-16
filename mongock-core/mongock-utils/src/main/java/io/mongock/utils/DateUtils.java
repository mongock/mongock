package io.mongock.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {

  private DateUtils() {
  }

  public static Date toDate(Object value) {
    if (value == null) {
      return null;
    }
    else if (value.getClass().equals(Date.class)) {
      return (Date)value;
    }
    else if (value.getClass().equals(LocalDateTime.class)) {
      return localDateTimeToDate((LocalDateTime)value);
    }
    else if (value.getClass().equals(OffsetDateTime.class)) {
      return offsetDateTimeToDate((OffsetDateTime)value);
    }
    else {
      throw new RuntimeException(String.format("%s cannot be cast to %s", value.getClass().getName(), Date.class.getName()));
    }
  }

  private static Date localDateTimeToDate(LocalDateTime dateToConvert) {
    return Date
            .from(dateToConvert.atZone(ZoneId.systemDefault())
                    .toInstant());
  }

  private static Date offsetDateTimeToDate(OffsetDateTime dateToConvert) {
    return Date.from(dateToConvert.toInstant());
  }

}
