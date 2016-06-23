/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

public class ApiClientConfiguration {
    private final String apiBaseUrl;
    private final String applicationName;
    private final String signingKey;
    private final String signingSecret;
    private final String propositionName;
    private final String apiVersion;

    public ApiClientConfiguration(String apiBaseUrl, String applicationName, String propositionName, String signingKey, String signingSecret, String apiVersion) {
        this.apiBaseUrl = apiBaseUrl;
        this.applicationName = applicationName;
        this.propositionName = propositionName;
        this.signingKey = signingKey;
        this.signingSecret = signingSecret;
        this.apiVersion = apiVersion;
    }

    public ApiClientConfiguration(String signingKey, String signingSecret) {
        this.signingKey = signingKey;
        this.signingSecret = signingSecret;
        this.apiBaseUrl = null;
        this.applicationName = null;
        this.propositionName = null;
        this.apiVersion = null;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public String getSigningSecret() {
        return signingSecret;
    }

    public String getPropositionName() {
        return propositionName;
    }

    public String getApiVersion() {
        return apiVersion;
    }
}
