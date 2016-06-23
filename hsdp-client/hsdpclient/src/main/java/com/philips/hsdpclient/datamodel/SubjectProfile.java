/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.datamodel;

import com.philips.hsdpclient.Formattable;
import com.philips.hsdpclient.util.StringFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.HashMap;
import java.util.Map;

public class SubjectProfile implements Formattable {

    public SubjectProfile() {
        metadata = new HashMap<>();
    }

    public String guid;
    public String name;
    public String givenName;
    public String familyName;
    public String middleName;
    public DateTime birthday;
    public String gender;
    public DateTime lastUpdated;
    public Map<String, String> metadata;

    public DateTime getDateTimeMetaData(String key, String pattern) {
        String dateString = metadata.get(key);
        DateTime dateValue = null;
        if (dateString != null && !dateString.equals("")) {
            dateValue = DateTime.parse(dateString, DateTimeFormat.forPattern(pattern).withOffsetParsed());
        }
        return dateValue;
    }

    public DateTime getDateMetaData(String key, String pattern) {
        String dateString = metadata.get(key);
        DateTime dateValue = null;
        if (dateString != null && !dateString.equals("")) {
            dateValue = DateTime.parse(dateString, DateTimeFormat.forPattern(pattern).withZoneUTC());
        }
        return dateValue;
    }

    public double getDoubleMetaData(String key) {
        String doubleString = metadata.get(key);
        double doubleValue = 0.0;
        if (doubleString != null && !doubleString.equals("")) {
            doubleValue = Double.parseDouble(doubleString);
        }
        return doubleValue;
    }

    public boolean getBooleanMetaData(String key) {
        String booleanString = metadata.get(key);
        boolean booleanValue = false;
        if (booleanString != null && !booleanString.equals("")) {
            booleanValue = Boolean.parseBoolean(booleanString);
        }
        return booleanValue;
    }

    public Integer getIntegerMetaData(String key) {
        String integerString = metadata.get(key);
        Integer integerValue = 0;
        if (integerString != null && !integerString.equals("")) {
            integerValue = Integer.parseInt(integerString);
        }
        return integerValue;
    }

    @Override
    public String toString(StringFormatter stringFormatter) {
        return stringFormatter.format(this);
    }
}
