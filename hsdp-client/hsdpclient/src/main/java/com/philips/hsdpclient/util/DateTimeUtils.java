/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter HSDP_OBSERVATION_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.00'Z'").withZoneUTC();
    private static final DateTimeFormatter HSDP_PARAMETER_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").withZoneUTC();

    public static DateTime toDateTime(String value) {
        if (value == null || value.equals(""))
            return null;
        try {
            return DATE_TIME_FORMATTER.parseDateTime(value);
        } catch (IllegalArgumentException ex) {
            return DATE_FORMATTER.parseDateTime(value);
        }
    }

    public static DateTime toObservationDateTime(String value) {
        if (value == null || value.equals(""))
            return null;
        return HSDP_OBSERVATION_DATE_TIME_FORMATTER.parseDateTime(value);
    }

    public static String asString(DateTime dateTime) {
        return HSDP_PARAMETER_DATE_TIME_FORMATTER.print(dateTime);
    }

    public static String asObservationString(DateTime dateTime) {
        return dateTime.withMillisOfSecond(0).toString(HSDP_OBSERVATION_DATE_TIME_FORMATTER);
    }
}