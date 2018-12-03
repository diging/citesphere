package edu.asu.diging.citesphere.core.util;

import java.time.OffsetDateTime;

public interface IDateParser {

    OffsetDateTime parse(String dateString);

}