package net.unstream.fractal.db.converters;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

public class LongToJodaDateTimeConverter implements Converter<Long, DateTime> {
    public DateTime convert(Long millis) {
        return new DateTime(millis);
    }
}