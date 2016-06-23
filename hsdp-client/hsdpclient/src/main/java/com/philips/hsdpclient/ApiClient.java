/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.philips.hsdpclient.response.Response;

public class ApiClient extends RestApiClient {
    protected static final String RESPONSE_OK = "200";
    private final ApiSigner apiSigner;
    protected final String applicationName;
    protected final String propositionName;
    protected final String apiVersion;

    public ApiClient(ApiClientConfiguration clientConfiguration) {
        super(clientConfiguration.getApiBaseUrl());
        applicationName = clientConfiguration.getApplicationName();
        propositionName = clientConfiguration.getPropositionName();
        apiVersion = clientConfiguration.getApiVersion();
        apiSigner = new ApiSigner(clientConfiguration.getSigningKey(), clientConfiguration.getSigningSecret());
    }

    protected Response sendRequest(HttpMethod httpMethod, String apiEndpoint, String queryParams, HttpHeaders headers, Object body) {
        return new Response(sendRestRequest(httpMethod, apiEndpoint, queryParams, headers, body));
    }

    protected void sign(HttpHeaders headers, String url, String queryParams, HttpMethod httpMethod, Object body) {
        sign(headers, url, queryParams, httpMethod, body, DateTime.now(DateTimeZone.UTC));
    }

    protected void sign(HttpHeaders headers, String url, String queryParams, HttpMethod httpMethod, Object body, DateTime now) {
        String date = now.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        headers.add("SignedDate", date);
        String authHeaderValue = apiSigner.buildAuthorizationHeaderValue(httpMethod, queryParams, headers, url, createBodyString(body));
        headers.add(com.google.common.net.HttpHeaders.AUTHORIZATION, authHeaderValue);
    }

    private String createBodyString(Object body) {
        if (body == null)
            return "";

        return asJsonString(body);
    }
}
