package edu.asu.diging.citesphere.core.util.impl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.util.IDateParser;

@Component
public class DateParser implements IDateParser {
    
    private Map<String, String> dateFormats;
    
    @PostConstruct
    public void init() {
        dateFormats = new HashMap<String, String>();
        dateFormats.put("[0-9]{3,4}", "yyyy");
        dateFormats.put("[0-1]?[0-9]/[0-3]?[0-9]/[0-9]{3,4}", "MM/dd/yyyy");
        dateFormats.put("[0-3]?[0-9]/[0-1]?[0-9]/[0-9]{3,4}", "dd/MM/yyyy");
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.util.impl.IDateParser#parse(java.lang.String)
     */
    @Override
    public OffsetDateTime parse(String dateString) {
        for (String regex : dateFormats.keySet()) {
            if (dateString.matches(regex)) {
               DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(dateFormats.get(regex))
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .toFormatter();
               LocalDate time = LocalDate.parse(dateString, formatter);
               return time.atTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC));
            }
        }
        return null;
    }
}
