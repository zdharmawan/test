/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.base.MoreObjects;

public class TermsAndConditionResponse {

    public String documentVersion;
    public String deviceIdentificationNumber;
    public String standardObservationName;
    public String applicationName;
    public String consentStatus;
    public DateTime startDate;

    public TermsAndConditionResponse() {
        startDate = new DateTime("1970-1-1", DateTimeZone.UTC);
    }

    public TermsAndConditionResponse(String documentVersion, String deviceIdentificationNumber, String standardObservationName, String consentStatus, String applicationName,
            DateTime startDate) {
        this();
        this.documentVersion = documentVersion;
        this.deviceIdentificationNumber = deviceIdentificationNumber;
        this.standardObservationName = standardObservationName;
        this.applicationName = applicationName;
        this.consentStatus = consentStatus;
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TermsAndConditionResponse that = (TermsAndConditionResponse) o;

        if (documentVersion != null ? !documentVersion.equals(that.documentVersion) : that.documentVersion != null)
            return false;
        if (deviceIdentificationNumber != null ? !deviceIdentificationNumber.equals(that.deviceIdentificationNumber) : that.deviceIdentificationNumber != null)
            return false;
        if (standardObservationName != null ? !standardObservationName.equals(that.standardObservationName) : that.standardObservationName != null)
            return false;
        if (applicationName != null ? !applicationName.equals(that.applicationName) : that.applicationName != null)
            return false;
        if (consentStatus != null ? !consentStatus.equals(that.consentStatus) : that.consentStatus != null)
            return false;
        return startDate != null ? startDate.equals(that.startDate) : that.startDate == null;

    }

    @Override
    public int hashCode() {
        int result = documentVersion != null ? documentVersion.hashCode() : 0;
        result = 31 * result + (deviceIdentificationNumber != null ? deviceIdentificationNumber.hashCode() : 0);
        result = 31 * result + (standardObservationName != null ? standardObservationName.hashCode() : 0);
        result = 31 * result + (applicationName != null ? applicationName.hashCode() : 0);
        result = 31 * result + (consentStatus != null ? consentStatus.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("acceptedTermsVersion", documentVersion)
                .add("standardObservationName", standardObservationName)
                .add("applicationName", applicationName)
                .add("consentStatus", consentStatus)
                .add("startDate", startDate)
                .add("deviceIdentificationNumber", deviceIdentificationNumber)
                .toString();
    }
}
