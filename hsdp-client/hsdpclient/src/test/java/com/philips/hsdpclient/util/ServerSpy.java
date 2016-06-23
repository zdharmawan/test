/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import java.net.URI;

import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ServerSpy extends RestTemplate {

    private HttpHeaders sentHeaders;
    public String sentBody;
    public HttpMethod httpMethod;

    @Override
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        if (requestEntity.getBody() == null)
            sentBody = null;
        else
            sentBody = requestEntity.getBody().toString();

        sentHeaders = requestEntity.getHeaders();
        httpMethod = method;
        return super.exchange(url, method, requestEntity, responseType);
    }

    public String getSentHeader(String name) {
        return sentHeaders.toSingleValueMap().get(name);
    }
}
