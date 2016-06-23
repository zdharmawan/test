/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;

import com.philips.hsdpclient.response.ForwarderResponse;
import com.philips.hsdpclient.util.ServerSpy;

public class ForwardingClientTest extends BaseClientTest {

    private ForwarderResponse response;
    private DateTime now;

    @Test
    public void forwardsRawRequestBody() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY);
        whenMakingRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheSentBodyIs(REQUEST_BODY);
    }

    @Test
    public void signsRawRequest() {
        givenTheTimeIs(DateTime.parse("2016-02-16T12:34:56.123Z").withZone(DateTimeZone.UTC));
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY);
        whenMakingRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheAuthorizationHeaderIs("HmacSHA256;Credential:;SignedHeaders:SignedDate;Signature:a0cB0DSoqRWbwTs1KEl4h1lSlObzdVEmns9LoKHn9+Y=");
    }

    @Test
    public void forwardsBackBadRequestException() {
        givenTheServerRespondsWithBadRequest(URL + "?" + PARAMETERS);
        givenTheExpectedException(HttpClientErrorException.class);
        whenMakingRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheExpectedErrorMessageIs(BAD_REQUEST_ERROR_MESSAGE);
    }

    @Test
    public void returnsResponseBody() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY);
        whenMakingRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedBodyIs(RESPONSE_BODY);
    }

    @Test
    public void returnsResponseHeaders() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY, responseHeaders);
        whenMakingRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedHeadersAre(expectedResponseHeaders);
    }

    @Test
    public void returnsStatusCode() {
        givenTheServerRespondsWithNoContent(URL + "?" + PARAMETERS);
        whenMakingRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedStatusCodeIs(HttpStatus.NO_CONTENT);
    }

    @Test
    public void plainForwardsRawRequestBody() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY);
        whenMakingPlainRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheSentBodyIs(REQUEST_BODY);
    }

    @Test
    public void plainForwardsRawRequestHeaders() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY, responseHeaders);
        whenMakingPlainRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedHeadersAre(expectedResponseHeaders);
    }

    @Test
    public void plainReturnsStatusCode() {
        givenTheServerRespondsWithNoContent(URL + "?" + PARAMETERS);
        whenMakingPlainRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedStatusCodeIs(HttpStatus.NO_CONTENT);
    }

    @Test
    public void plainReturnsResponseBody() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY);
        whenMakingPlainRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedBodyIs(RESPONSE_BODY);
    }

    @Test
    public void plainReturnsResponseHeaders() {
        givenTheServerRespondsWith(URL + "?" + PARAMETERS, RESPONSE_BODY, responseHeaders);
        whenMakingPlainRequest(HttpMethod.GET, HOST, ENDPOINT, PARAMETERS, HEADERS, REQUEST_BODY);
        thenTheReturnedHeadersAre(expectedResponseHeaders);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        forwarder = new ForwardingClient(clientConfiguration);
        forwarder.setRestTemplate(serverSpy);
        now = DateTime.now(DateTimeZone.UTC);

        responseHeaders.put(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE);

        expectedResponseHeaders.put("Content-Type", Collections.singletonList("application/json"));
        expectedResponseHeaders.put(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE);
    }

    private void givenTheTimeIs(DateTime dateTime) {
        now = dateTime;
    }

    private void givenTheServerRespondsWithBadRequest(String url) {
        server.expect(requestTo(url)).andRespond(withBadRequest());
    }

    private void givenTheServerRespondsWith(String url, String responseBody, HttpHeaders responseHeaders) {
        server.expect(requestTo(url)).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON).headers(responseHeaders));
    }

    private void givenTheServerRespondsWithNoContent(String url) {
        server.expect(requestTo(url)).andRespond(withNoContent());
    }

    private void givenTheExpectedException(Class expectedException) {
        thrown.expect(expectedException);
    }

    private void whenMakingRequest(HttpMethod method, String host, String endpoint, String parameters, HttpHeaders headers, String body) {
        response = forwarder.signAndForwardRequest(method, host, endpoint, parameters, headers, body, now);
    }

    private void whenMakingPlainRequest(HttpMethod method, String host, String endpoint, String parameters, HttpHeaders headers, String requestBody) {
        response = forwarder.forwardRequest(method, host, endpoint, parameters, headers, requestBody);
    }

    private void thenTheReturnedBodyIs(String expectedBody) {
        assertEquals(expectedBody, response.body);
    }

    private void thenTheAuthorizationHeaderIs(String expectedHeader) {
        assertEquals(expectedHeader, serverSpy.getSentHeader(com.google.common.net.HttpHeaders.AUTHORIZATION));
    }

    private void thenTheReturnedStatusCodeIs(HttpStatus expectedStatus) {
        assertEquals(expectedStatus, response.status);
    }

    private void thenTheExpectedErrorMessageIs(String errorMessage) {
        thrown.expectMessage(errorMessage);
    }

    private void thenTheReturnedHeadersAre(HttpHeaders expectedHeaders) {
        assertEquals(expectedHeaders, response.headers);
    }

    private static final String HOST = "https://dummy.org";
    private static final String ENDPOINT = "/somePath";
    private static final String URL = HOST + ENDPOINT;

    private static final String PARAMETERS = "dontCare=true&whatever=false";

    private HttpHeaders HEADERS = new HttpHeaders();

    private static final String RESPONSE_HEADER_NAME = "HeaderName";
    private static final List<String> RESPONSE_HEADER_VALUE = Collections.singletonList("HeaderValue");

    private HttpHeaders responseHeaders = new HttpHeaders();
    private HttpHeaders expectedResponseHeaders = new HttpHeaders();

    private static final String REQUEST_BODY = "myRequestBody";
    private static final String RESPONSE_BODY = "noBodyYet";
    private static final String BAD_REQUEST_ERROR_MESSAGE = "400 Bad Request";
    private ForwardingClient forwarder;
}
