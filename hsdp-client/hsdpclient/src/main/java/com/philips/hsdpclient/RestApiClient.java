/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import java.net.URI;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.philips.hsdpclient.response.ForwarderResponse;
import com.philips.hsdpclient.response.RestResponse;

public class RestApiClient {
    private final static ObjectMapper JSON_MAPPER;
    private RestTemplate restTemplate;
    private String apiBaseUrl;

    static {
        JSON_MAPPER = new ObjectMapper();
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public RestApiClient(String apiBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charsets.UTF_8));
        this.apiBaseUrl = apiBaseUrl;
    }

    public Map<String, Object> sendRestRequest(HttpMethod httpMethod, String apiEndpoint, String queryParams, HttpHeaders headers, Object body) {
        String bodyString = asJsonString(body);
        URI uri = URI.create(apiBaseUrl + apiEndpoint + queryParams(queryParams));
        return sendRestRequest(httpMethod, uri, headers, bodyString);
    }

    public RestResponse sendRestRequestAndGetResponse(HttpMethod httpMethod, String apiEndpoint, String queryParams, HttpHeaders headers, Object body) {
        return sendRestRequestAndGetResponse(httpMethod, apiBaseUrl, apiEndpoint, queryParams, headers, body);
    }

    public RestResponse sendRestRequestAndGetResponse(HttpMethod httpMethod, String baseUrl, String apiEndpoint, String queryParams, HttpHeaders headers, Object body) {
        String bodyString = asJsonString(body);
        URI uri = URI.create(baseUrl + apiEndpoint + queryParams(queryParams));
        return sendRestRequestAndGetResponse(httpMethod, uri, headers, bodyString);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> sendRestRequest(HttpMethod httpMethod, URI uri, HttpHeaders headers, Object body) {
        RestResponse restResponse = sendRestRequestAndGetResponse(httpMethod, uri, headers, body);
        return restResponse.body;
    }

    private RestResponse sendRestRequestAndGetResponse(HttpMethod httpMethod, URI uri, HttpHeaders headers, Object body) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(
                    uri,
                    httpMethod,
                    new HttpEntity<>(body, headers),
                    Map.class);

            return new RestResponse(exchange.getBody(), exchange.getHeaders().toSingleValueMap());
        } catch (RestClientException e) {
            throw new CommunicationException(e);
        }
    }

    protected ForwarderResponse forwardRestRequest(HttpMethod httpMethod, URI uri, HttpHeaders headers, Object body) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> exchange = restTemplate.exchange(
                uri,
                httpMethod,
                new HttpEntity<>(body, headers),
                String.class);
        return new ForwarderResponse(exchange.getHeaders(), exchange.getBody(), exchange.getStatusCode());
    }

    protected String queryParams(String queryParamString) {
        if (Strings.isNullOrEmpty(queryParamString))
            return "";

        return "?" + queryParamString;
    }

    public String asJsonString(Object request) {
        if (request == null)
            return null;
        try {
            if (request.getClass().equals(String.class))
                return (String) request;

            return JSON_MAPPER.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
