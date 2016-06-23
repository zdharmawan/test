/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

public class ApiClientConfigurationBasicAuthentication {
    private final String apiBaseUrl;
    private final String username;
    private final String password;
    private final String apiVersion;

    public ApiClientConfigurationBasicAuthentication(String apiBaseUrl, String username, String password, String apiVersion) {
        this.apiBaseUrl = apiBaseUrl;
        this.username = username;
        this.password = password;
        this.apiVersion = apiVersion;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getApiVersion() {
        return apiVersion;
    }

}
