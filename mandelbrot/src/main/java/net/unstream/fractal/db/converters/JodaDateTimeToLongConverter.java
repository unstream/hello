package net.unstream.fractal.db.converters;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

public class JodaDateTimeToLongConverter implements Converter<DateTime, Long> {
    public Long convert(DateTime dateTime) {
        return dateTime.getMillis();
    }
}
 