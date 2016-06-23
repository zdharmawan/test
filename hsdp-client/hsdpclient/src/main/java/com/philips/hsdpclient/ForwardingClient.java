/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import java.net.URI;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.philips.hsdpclient.response.ForwarderResponse;

public class ForwardingClient extends ApiClient {

    public ForwardingClient(ApiClientConfiguration clientConfiguration) {
        super(clientConfiguration);
    }

    public ForwarderResponse signAndForwardRequest(HttpMethod method, String host, String endpoint, String parameters, HttpHeaders headers, Object body) {
        return signAndForwardRequest(method, host, endpoint, parameters, headers, body, DateTime.now(DateTimeZone.UTC));
    }

    public ForwarderResponse signAndForwardRequest(HttpMethod method, String host, String endpoint, String parameters, HttpHeaders headers, Object body, DateTime date) {
        sign(headers, endpoint, parameters, method, body, date);
        return forwardRequest(method, host + endpoint, parameters, headers, body);
    }

    public ForwarderResponse forwardRequest(HttpMethod method, String host, String endpoint, String parameters, HttpHeaders headers, Object body) {
        return forwardRequest(method, host + endpoint, parameters, headers, body);
    }

    private ForwarderResponse forwardRequest(HttpMethod method, String url, String parameters, HttpHeaders headers, Object body) {
        URI uri = URI.create(url + queryParams(parameters));
        return forwardRestRequest(method, uri, headers, body);
    }
}
