/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

public class StoreTermsAndConditionsRequest {
    public final String applicationName;
    public final String documentId;
    public final String documentVersion;
    public final String countryCode;
    public final String consentCode;
    public final String standardObservationName;
    public final String propositionName;
    public final String consentStatus;
    public final String deviceIdentificationNumber;

    public StoreTermsAndConditionsRequest(String applicationName, String documentId, String documentVersion, String countryCode, String consentCode, String standardObservationName,
            String propositionName, String consentStatus, String deviceIdentificationNumber) {
        this.applicationName = applicationName;
        this.documentId = documentId;
        this.documentVersion = documentVersion;
        this.countryCode = countryCode;
        this.consentCode = consentCode;
        this.standardObservationName = standardObservationName;
        this.propositionName = propositionName;
        this.consentStatus = consentStatus;
        this.deviceIdentificationNumber = deviceIdentificationNumber;
    }
}
