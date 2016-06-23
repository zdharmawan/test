/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.logging;

import com.philips.hsdpclient.Formattable;

public class LogMessageBuilder {

    private final StringBuilder logMessage;
    private final LogStringFormatter logStringFormatter;
    private final String OPENING_TAG = "[";
    private final String CLOSING_TAG = "]";

    private LogMessageBuilder(String message) {
        logStringFormatter = new LogStringFormatter();
        logMessage = new StringBuilder();
        logMessage.append((message == null ? "ERROR null message logged" : message)).append(":").append(OPENING_TAG);
    }

    public static LogMessageBuilder createLogMessageBuilder(String message) {
        return new LogMessageBuilder(message);
    }

    public LogMessageBuilder appendDCPairingId(String dcPairingId) {
        append("DCPairingID", dcPairingId);
        return this;
    }

    public LogMessageBuilder appendSubjectId(String subjectId) {
        append("SubjectId", subjectId);
        return this;
    }

    public LogMessageBuilder appendAccessToken(String accessToken) {
        append("AccessToken", accessToken);
        return this;
    }

    public LogMessageBuilder appendUserId(String userId) {
        append("UserID", userId);
        return this;
    }

    public LogMessageBuilder appendApplicationName(String applicationName) {
        append("ApplicationName", applicationName);
        return this;
    }

    public LogMessageBuilder appendPropositionName(String propositionName) {
        append("PropositionName", propositionName);
        return this;
    }

    public LogMessageBuilder appendStandardObservationName(String standardObservationName) {
        append("StandardObservationName", standardObservationName);
        return this;
    }

    public LogMessageBuilder appendConsentStatus(String consentStatus) {
        append("ConsentStatus", consentStatus);
        return this;
    }

    public LogMessageBuilder appendConsentCode(String consentCode) {
        append("ConsentCode", consentCode);
        return this;
    }

    public LogMessageBuilder appendCountryCode(String countryCode) {
        append("CountryCode", countryCode);
        return this;
    }

    public LogMessageBuilder appendDocumentVersion(String documentVersion) {
        append("DocumentVersion", documentVersion);
        return this;
    }

    public LogMessageBuilder appendSubscriptionId(String userSubscriptionUUID) {
        append("UserSubscriptionUUID", userSubscriptionUUID);
        return this;
    }

    public LogMessageBuilder appendObservationIds(String observationIds) {
        append("ObservationIds", observationIds);
        return this;
    }

    public LogMessageBuilder appendStartTimeUtc(String startTimeUtc) {
        append("StartTime", startTimeUtc);
        return this;
    }

    public LogMessageBuilder appendErrorMessage(String errorMessage) {
        append("ErrorMessage", errorMessage);
        return this;
    }

    public LogMessageBuilder append(Formattable objectToFormat) {
        if (objectToFormat != null) {
            logMessage.append(objectToFormat.toString(logStringFormatter));
        }
        return this;
    }

    @Override
    public String toString() {
        return logMessage.toString() + CLOSING_TAG;
    }

    private LogMessageBuilder append(String name, String value) {
        logMessage.append((name == null ? "ERROR key not set" : name)).append(":").append(value).append(";");
        return this;
    }
}
