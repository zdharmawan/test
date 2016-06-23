/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.logging;

import java.util.List;
import java.util.Map;

import com.philips.hsdpclient.datamodel.DCRelation;
import com.philips.hsdpclient.datamodel.SubjectProfile;
import com.philips.hsdpclient.request.Photo;
import com.philips.hsdpclient.request.Profile;
import com.philips.hsdpclient.response.DCPairingResponse;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.util.StringFormatter;

public class LogStringFormatter implements StringFormatter {

    private final String OPENING_TAG = "[";
    private final String CLOSING_TAG = "]";
    private final String NULL = "null";
    private final String DELIMITER = ";";

    @Override
    public String format(Profile userProfile) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("UserProfile:" + OPENING_TAG);
        if (userProfile != null) {
            appendProperty(logMessage, "BirthDay", userProfile.birthday + DELIMITER);
            appendProperty(logMessage, "GivenName", userProfile.givenName + DELIMITER);
            appendProperty(logMessage, "MiddleName", userProfile.middleName + DELIMITER);
            appendProperty(logMessage, "FamilyName", userProfile.familyName + DELIMITER);
            appendProperty(logMessage, "DisplayName", userProfile.displayName + DELIMITER);
            appendProperty(logMessage, "Gender", userProfile.gender + DELIMITER);
            appendProperty(logMessage, "CurrentLocation", userProfile.currentLocation + DELIMITER);
            appendProperty(logMessage, "Height", userProfile.height + DELIMITER);
            appendProperty(logMessage, "Weight", userProfile.weight + DELIMITER);
            appendProperty(logMessage, "Locale", userProfile.locale + DELIMITER);
            appendProperty(logMessage, "TimeZone", userProfile.timeZone + DELIMITER);
            appendProperty(logMessage, "PreferredLanguage", userProfile.preferredLanguage + DELIMITER);
            if (userProfile.primaryAddress != null) {
                appendProperty(logMessage, "PrimaryAddress", userProfile.primaryAddress.country + DELIMITER);
            }
            logMessage.append(format(userProfile.photos));
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    @Override
    public String format(SubjectProfile subjectProfile) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("SubjectProfile:" + OPENING_TAG);
        if (subjectProfile != null) {
            appendProperty(logMessage, "SubjectID", subjectProfile.guid);
            appendProperty(logMessage, "Name", subjectProfile.name);
            appendProperty(logMessage, "GivenName", subjectProfile.givenName);
            appendProperty(logMessage, "FamilyName", subjectProfile.familyName);
            appendProperty(logMessage, "MiddleName", subjectProfile.middleName);
            appendProperty(logMessage, "Birthday", subjectProfile.birthday);
            appendProperty(logMessage, "Gender", subjectProfile.gender);
            appendProperty(logMessage, "LastUpdated", subjectProfile.lastUpdated);
            logMessage.append(format(subjectProfile.metadata));
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    @Override
    public String format(Response response) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("HSDPResponse:" + OPENING_TAG);
        if (response != null) {
            appendProperty(logMessage, "Message", response.message);
            appendProperty(logMessage, "Code", response.code);
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    @Override
    public String format(Photo photo) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Photo:" + OPENING_TAG);
        if (photo != null) {
            appendProperty(logMessage, "Type", photo.type);
            appendProperty(logMessage, "Base64Value", photo.value);
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    @Override
    public String format(DCRelation dcRelation) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("DCRelation:" + OPENING_TAG);
        if (dcRelation != null) {
            appendProperty(logMessage, "Trustor", dcRelation.trustor.value);
            appendProperty(logMessage, "Trustee", dcRelation.trustee.value);
            appendProperty(logMessage, "ExpireDate", dcRelation.expireDate);
            appendProperty(logMessage, "Type", dcRelation.type.value);
            appendProperty(logMessage, "Metadata", dcRelation.metadata);
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    @Override
    public String format(DCPairingResponse dcPairingResponse) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("DCPairingResponse:" + OPENING_TAG);
        if (dcPairingResponse != null) {
            appendProperty(logMessage, "Message", dcPairingResponse.message);
            appendProperty(logMessage, "Code", dcPairingResponse.code);
            appendProperty(logMessage, "Headers", dcPairingResponse.headers);
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    private String format(Map<String, String> metadata) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Metadata:" + OPENING_TAG);
        if (metadata != null) {
            for (String key : metadata.keySet()) {
                appendProperty(logMessage, key, metadata.get(key));
            }
        } else {
            logMessage.append(NULL);
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    private String format(List<Photo> photos) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Photos:" + OPENING_TAG);
        if (photos != null) {
            for (Photo photo : photos) {
                format(photo);
            }
        }
        logMessage.append(CLOSING_TAG);
        return logMessage.toString();
    }

    private void appendProperty(StringBuilder logMessage, String name, Object value) {
        logMessage.append(name + ":" + value + DELIMITER);
    }
}
