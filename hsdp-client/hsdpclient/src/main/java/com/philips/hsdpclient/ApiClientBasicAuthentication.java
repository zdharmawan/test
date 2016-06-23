/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.philips.hsdpclient.response.DCPairingResponse;

public class ApiClientBasicAuthentication extends RestApiClient {
    protected static final String RESPONSE_CREATED_OK = "201";
    protected static final String RESPONSE_OK = "200";

    protected final String apiVersion;
    protected final String basicAuthorizationHeader;

    public ApiClientBasicAuthentication(ApiClientConfigurationBasicAuthentication clientConfiguration) {
        super(clientConfiguration.getApiBaseUrl());

        if (clientConfiguration.getApiBaseUrl() == null)
            throw new IllegalArgumentException("Missing API URL");

        if (clientConfiguration.getUsername() == null)
            throw new IllegalArgumentException("Missing API Basic Authentication Username");

        apiVersion = clientConfiguration.getApiVersion();
        basicAuthorizationHeader = "Basic " +
                                   encodeBase64String(clientConfiguration.getUsername() + ":" + clientConfiguration.getPassword());
    }

    public String encodeBase64String(String encodingText) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(encodingText));
    }

    protected DCPairingResponse sendRequest(HttpMethod httpMethod, String apiEndpoint, String queryParams, HttpHeaders headers, Object body) {
        headers.add(com.google.common.net.HttpHeaders.AUTHORIZATION, basicAuthorizationHeader);
        return new DCPairingResponse(sendRestRequestAndGetResponse(httpMethod, apiEndpoint, queryParams, headers, body));
    }

}
